package com.didiglobal.knowframework.log.log4j2.appender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.observability.common.bean.Log;
import com.didiglobal.knowframework.observability.common.bean.Span;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import com.didiglobal.knowframework.observability.common.enums.LogEventType;
import io.opentelemetry.api.common.AttributeKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Plugin(name = ElasticsearchAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class ElasticsearchAppender extends AbstractAppender {

    public static final String PLUGIN_NAME = "ElasticsearchAppender";

    private static final String           COMMA             = ",";
    private static final String TYPE_NAME_DEFAULT_VALUE = "type";
    private static final String THRESHOLD_DEFAULT_VALUE = "all";
    private static final Integer BUFFER_SIZE_DEFAULT_VALUE = 1000;
    private static final Integer NUMBER_OF_SHARDS_DEFAULT_VALUE = 1;
    private static final Integer NUMBER_OF_REPLICAS_DEFAULT_VALUE = 1;
    private static final Integer LOG_EXPIRE_DEFAULT_VALUE = 7;

    /**
     * elasticsearch address
     */
    private String address;
    /**
     * elasticsearch port
     */
    private Integer port;
    /**
     * elasticsearch user
     */
    private String user;
    /**
     * elasticsearch password
     */
    private String password;
    /**
     * elasticsearch index name
     */
    private String indexName;
    /**
     * elasticsearch type name
     */
    private String typeName;
    /**
     * index shard 数
     */
    private Integer numberOfShards;
    /**
     * index replicas 数
     */
    private Integer numberOfReplicas;
    /**
     * 日志输出级别，可选择 info、debug、warn、error、all（default value）
     */
    private String threshold;
    /**
     * 日志缓冲池大小,如果缓冲池大小为1则日志会被立即同步到ES中</br>
     * 否则需要等到缓冲池Size达到bufferSize了后才会将日志刷新至ES</br>
     * bufferSize默认初始化为1
     */
    private Integer bufferSize;
    /**
     * 操作ES集群的客户端
     */
    private RestHighLevelClient client;
    /**
     * 日志缓冲数据
     */
    private BlockingQueue<IndexRequest> buffer;
    /**
     * log event 刷写线程
     */
    private ExecutorService threadPool;
    /**
     * 日志过期阈值，单位：日
     */
    private Integer logExpire;

    private volatile CompletableFuture<?> availableFuture = new CompletableFuture<>();
    private volatile CompletableFuture<?> notAvailableFuture = new CompletableFuture<>();

    private volatile Boolean sendLogEvent2ElasticsearchRunnableSwitch;
    private volatile Boolean elasticsearchLogCleanRunnableSwitch;

    public ElasticsearchAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout,
            String address,
            Integer port,
            String user,
            String password,
            String indexName,
            String typeName,
            Integer numberOfShards,
            Integer numberOfReplicas,
            String threshold,
            Integer bufferSize,
            Integer logExpire) {
        super(name, filter, layout);
        this.address = address;
        this.port = port;
        this.user = user;
        this.password = password;
        this.indexName = indexName;
        this.typeName = getTypeName(typeName);
        this.numberOfShards = getNumberOfShards(numberOfShards);
        this.numberOfReplicas = getNumberOfReplicas(numberOfReplicas);
        this.threshold = getThreshold(threshold);
        this.bufferSize = getBufferSize(bufferSize);
        this.logExpire = getLogExpire(logExpire);
        //初始化缓冲区
        this.buffer = new LinkedBlockingQueue<>(bufferSize);
        //初始化 elasticsearch client
        this.client = getRestHighLevelClient();
        //校验 index 是否已创建，如未创建，则进行创建 创建 时 采用 给 定 elasticsearch mappings.
        verifyAndCreateElasticsearchIndexIfNotExists();
        //构建 elasticsearch 缓冲区刷写线程
        if(null == threadPool) {
            threadPool = new ThreadPoolExecutor(
                    2,
                    2,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new CustomizableThreadFactory("Log4jElasticsearchAppenderThreadPool")
            );
            sendLogEvent2ElasticsearchRunnableSwitch = Boolean.TRUE;
            elasticsearchLogCleanRunnableSwitch = Boolean.TRUE;
            threadPool.execute(new SendLogEvent2ElasticsearchRunnable());
            threadPool.execute(new ElasticsearchLogCleanRunnable(this.indexName, this.client));
        }
    }

    private Integer getLogExpire(Integer logExpire) {
        if(null == logExpire || 0 >= logExpire) {
            return LOG_EXPIRE_DEFAULT_VALUE;
        } else {
            return logExpire;
        }
    }

    private Integer getNumberOfShards(Integer numberOfShards) {
        if(null == numberOfShards || 0 >= numberOfShards) {
            return NUMBER_OF_SHARDS_DEFAULT_VALUE;
        } else {
            return numberOfShards;
        }
    }

    private Integer getNumberOfReplicas(Integer numberOfReplicas) {
        if(null == numberOfReplicas || 0 >= numberOfReplicas) {
            return NUMBER_OF_REPLICAS_DEFAULT_VALUE;
        } else {
            return numberOfReplicas;
        }
    }

    private void verifyAndCreateElasticsearchIndexIfNotExists() {
        try{
            if(!elasticsearchIndexExists()) {
                createElasticsearchIndex();
            } else {
                return;
            }
        } catch (IOException ex) {
            //index 校验出现异常 无须创建索引
            return;
        }
    }

    private void createElasticsearchIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", this.numberOfShards)
                .put("index.number_of_replicas", this.numberOfReplicas)
        );
        XContentBuilder mapping = getMapping();
        createIndexRequest.mapping(mapping);
        createIndexRequest.setTimeout(new TimeValue(1, TimeUnit.MINUTES));
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            if(!createIndexResponse.isAcknowledged()) {
                //索引创建 失 败 ignore
            }
        } catch (IOException ex) {
            //索引创建出现异常 ignore
        }
    }

    private XContentBuilder getMapping() throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject(Constant.ATTRIBUTE_KEY_SQL_STATEMENT)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_SQL_TYPE)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_HTTP_METHOD)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_COMPONENT)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_HTTP_URL)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_HTTP_SCHEMA)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_HTTP_HOST)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_HTTP_TARGET)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_JOB_CLASS_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.ATTRIBUTE_KEY_TASK_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_CLASS_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_FILE_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_LOG_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_METHOD_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_SPAN_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_TRACER_NAME)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_END_EPOCH_NANOS)
                            .field("type", "long")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_LINE_NUMBER)
                            .field("type", "long")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_LOG_LEVEL)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_LOG_MILLS)
                            .field("type", "date")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_LOG_THREAD)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_LOG_TYPE)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_MESSAGE)
                            .field("type", "text")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_PARENT_SPAN_ID)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_SPAN_ID)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_SPAN_KIND)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_SPENT_NANOS)
                            .field("type", "long")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_START_EPOCH_NANOS)
                            .field("type", "long")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_STATUS_DATA)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_TRACE_ID)
                            .field("type", "keyword")
                        .endObject()
                        .startObject(Constant.LOG_FIELD_NAME_TRACER_VERSION)
                            .field("type", "keyword")
                        .endObject()
                    .endObject()
                .endObject();
        return mapping;
    }

    private boolean elasticsearchIndexExists() throws IOException {
        GetIndexRequest exist=new GetIndexRequest(indexName);
        return client.indices().exists(exist, RequestOptions.DEFAULT);
    }

    class SendLogEvent2ElasticsearchRunnable implements Runnable {
        @Override
        public void run() {
            while (sendLogEvent2ElasticsearchRunnableSwitch) {
                if(CollectionUtils.isNotEmpty(buffer)) {
                    flushBuffer();
                } else {
                    resetAvailable();
                    try {
                        notAvailableFuture.get(50, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        //ignore
                    }
                }
            }
        }
    }

    class ElasticsearchLogCleanRunnable implements Runnable {

        // 每小时执行一次
        private static final long INTERVAL = 1 * 60 * 60 * 1000;
        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        private String indexName;
        private RestHighLevelClient client;

        public ElasticsearchLogCleanRunnable(String indexName, RestHighLevelClient client) {
            this.indexName = indexName;
            this.client = client;
        }

        @Override
        public void run() {
            while(elasticsearchLogCleanRunnableSwitch) {
                /*
                 * 间隔1小时进行一次 elasticsearch logs 清理
                 */
                try {
                    // 间隔一段时间执行一次
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                /*
                 * 删除日志
                 */
                try {
                    deleteByCreateTime(System.currentTimeMillis() - logExpire * 24 * 3600 * 1000);
                } catch (Exception ex) {

                }
            }
        }

        private void deleteByCreateTime(Long createTime) {
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
            deleteByQueryRequest.indices(this.indexName);
            deleteByQueryRequest.setQuery(
                    QueryBuilders.rangeQuery(Constant.LOG_FIELD_NAME_LOG_MILLS)
                    .lte(
                            dateFormat.format(createTime)
                    )
            );
            try {
                this.client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            } catch (IOException ex) {
                //ignore
            }
        }

    }

    @Override
    public void append(LogEvent event) {
        if (threshold.equalsIgnoreCase("all") || threshold.equalsIgnoreCase(event.getLevel().toString())) {
            processAndPutLogEventInBuffer(event);
        }
    }

    /**
     * 类结束之前调用方法
     */
    @Override
    public void stop() {
        try {
            if (buffer.size() > 0) {
                flushBuffer();
            }
            stopAllRunnable();
            this.client.close();
        } catch (Exception ex) {
            //ignore
        } finally {
            super.stop();
        }
    }

    private void stopAllRunnable() {
        this.sendLogEvent2ElasticsearchRunnableSwitch = Boolean.FALSE;
        this.elasticsearchLogCleanRunnableSwitch = Boolean.FALSE;
        this.threadPool.shutdownNow();
    }

    @PluginFactory
    public static ElasticsearchAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("address") String address,
            @PluginAttribute("port") Integer port,
            @PluginAttribute("user") String user,
            @PluginAttribute("password") String password,
            @PluginAttribute("indexName") String indexName,
            @PluginAttribute("typeName") String typeName,
            @PluginAttribute("numberOfShards") int numberOfShards,
            @PluginAttribute("numberOfReplicas") int numberOfReplicas,
            @PluginAttribute("threshold") String threshold,
            @PluginAttribute("bufferSize") int bufferSize,
            @PluginAttribute("logExpire") int logExpire,
            @PluginElement("Filter") final Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout
    ) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new ElasticsearchAppender(
                name,
                filter,
                layout,
                address,
                port,
                user,
                password,
                indexName,
                typeName,
                numberOfShards,
                numberOfReplicas,
                threshold,
                bufferSize,
                logExpire
        );
    }

    /**
     * 获取ES RestHighLevelClient客户端
     */
    private RestHighLevelClient getRestHighLevelClient() {
        if (client == null) {
            RestClientBuilder builder = RestClient.builder(new HttpHost(address, port, "http"));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                    .setDefaultIOReactorConfig(IOReactorConfig.custom()
                            .setSoKeepAlive(true)
                            .build())).setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                    .setConnectTimeout(40000)
                    .setSocketTimeout(40000));

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
            client = new RestHighLevelClient(builder);
        }
        return client;
    }

    private Integer getBufferSize(Integer bufferSize) {
        if(null == bufferSize || bufferSize.equals(0)) {
            return BUFFER_SIZE_DEFAULT_VALUE;
        }
        return bufferSize;
    }

    private String getThreshold(String threshold) {
        if(StringUtils.isBlank(threshold)) {
            return THRESHOLD_DEFAULT_VALUE;
        }
        if(
                !threshold.equalsIgnoreCase("all") &&
                        !threshold.equalsIgnoreCase("debug") &&
                        !threshold.equalsIgnoreCase("info") &&
                        !threshold.equalsIgnoreCase("warn") &&
                        !threshold.equalsIgnoreCase("error")
        ) {
            return THRESHOLD_DEFAULT_VALUE;
        }
        return threshold;
    }

    private String getTypeName(String typeName) {
        return StringUtils.isBlank(typeName) ? TYPE_NAME_DEFAULT_VALUE : typeName;
    }

    private void processAndPutLogEventInBuffer(LogEvent event) {
        Map<String, Object> element = logEvent2Map(event);
        if(null != element) {
            IndexRequest indexRequest = new IndexRequest(indexName).source(element);
            while (true) {
                boolean successful = buffer.offer(indexRequest);
                if(!successful) {// the buffer is full
                    resetUnavailable();
                    try {
                        availableFuture.get(50, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        //ignore
                    }
                } else {
                    if(!notAvailableFuture.isDone()) {
                        notAvailableFuture.complete(null);
                    }
                    break;
                }
            }
        }
    }

    private void resetUnavailable() {
        if (availableFuture.isDone()) {
            this.availableFuture = new CompletableFuture<>();
        }
    }

    private void resetAvailable() {
        if (notAvailableFuture.isDone()) {
            this.notAvailableFuture = new CompletableFuture<>();
        }
    }

    private Map<String, Object> logEvent2Map(LogEvent event) {
        Map<String, Object> item = new HashMap<>();
        /*
         * build env info
         */
        item.put(Constant.LOG_FIELD_NAME_CLASS_NAME, event.getSource().getClassName());
        item.put(Constant.LOG_FIELD_NAME_FILE_NAME, event.getSource().getFileName());
        item.put(Constant.LOG_FIELD_NAME_LINE_NUMBER, event.getSource().getLineNumber());
        item.put(Constant.LOG_FIELD_NAME_METHOD_NAME, event.getSource().getMethodName());
        item.put(Constant.LOG_FIELD_NAME_LOG_NAME, event.getLoggerName());
        item.put(Constant.LOG_FIELD_NAME_LOG_LEVEL, event.getLevel().toString());
        item.put(Constant.LOG_FIELD_NAME_LOG_THREAD, event.getThreadName());
        item.put(Constant.LOG_FIELD_NAME_LOG_MILLS, new Date(event.getTimeMillis()));
        /*
         * build log info
         */
        String message = event.getMessage().getFormattedMessage();
        try {
            JSONObject jsonObject = JSON.parseObject(message);
            if(null != jsonObject) {
                String logEventType = jsonObject.getObject("logEventType", String.class);
                if(LogEventType.LOG.name().equals(logEventType)) {
                    Log log = jsonObject.getObject("data", Log.class);
                    item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.LOG.name());
                    putLog(log, item);
                } else if(LogEventType.TRACE.name().equals(logEventType)) {
                    Span span = jsonObject.getObject("data", Span.class);
                    item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.TRACE.name());
                    putSpan(span, item);
                } else if(LogEventType.METRIC.name().equals(logEventType)) {
                    // TODO：
                } else {
                    // other whise
                    item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.LOG.name());
                    item.put(Constant.LOG_FIELD_NAME_MESSAGE, message);
                }
            } else {
                item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.LOG.name());
                item.put(Constant.LOG_FIELD_NAME_MESSAGE, message);
            }
        } catch (Exception ex) {
            // process message error
            item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.LOG.name());
            item.put(Constant.LOG_FIELD_NAME_MESSAGE, message);
        }
        return item;
    }

    private void putSpan(Span span, Map<String, Object> item) {
        item.put(Constant.LOG_FIELD_NAME_SPAN_NAME, span.getSpanName());
        item.put(Constant.LOG_FIELD_NAME_TRACE_ID, span.getTraceId());
        item.put(Constant.LOG_FIELD_NAME_SPAN_ID, span.getSpanId());
        item.put(Constant.LOG_FIELD_NAME_SPAN_KIND, span.getSpanKind().name());
        item.put(Constant.LOG_FIELD_NAME_PARENT_SPAN_ID, span.getParentSpanId());
        item.put(Constant.LOG_FIELD_NAME_START_EPOCH_NANOS, span.getStartEpochNanos());
        item.put(Constant.LOG_FIELD_NAME_END_EPOCH_NANOS, span.getEndEpochNanos());
        item.put(Constant.LOG_FIELD_NAME_STATUS_DATA, span.getStatusData().name());
        item.put(Constant.LOG_FIELD_NAME_SPENT_NANOS, span.getSpentNanos());
        item.put(Constant.LOG_FIELD_NAME_TRACER_NAME, span.getTracerName());
        item.put(Constant.LOG_FIELD_NAME_TRACER_VERSION, span.getTracerVersion());
        if(MapUtils.isNotEmpty(span.getAttributes())) {
            for (Map.Entry<AttributeKey<?>, Object> entry : span.getAttributes().entrySet()) {
                AttributeKey<?> key = entry.getKey();
                Object value = entry.getValue();
                item.put(key.getKey(), value);
            }
        }
    }

    private void putLog(Log log, Map<String, Object> item) {
        item.put(Constant.LOG_FIELD_NAME_TRACE_ID, log.getTracerId());
        item.put(Constant.LOG_FIELD_NAME_SPAN_ID, log.getSpanId());
        item.put(Constant.LOG_FIELD_NAME_MESSAGE, log.getMessage());
    }

    /**
     * 将数据写入到ES中
     */
    private synchronized void flushBuffer() {
        BulkRequest bulkRequest = new BulkRequest();
        Integer bulkRequestSize = 0;
        for (int i = 0; i < buffer.size(); i++) {
            IndexRequest indexRequest = null;
            try {
                indexRequest = buffer.poll(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if(null != indexRequest) {
                if(!availableFuture.isDone()) {
                    availableFuture.complete(null);
                }
                bulkRequest.add(indexRequest);
                bulkRequestSize++;
            } else {
                break;
            }
        }
        if(bulkRequestSize > 0) {
            try {
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            } catch (IOException ex) {
                //ignore
            }
        }
    }

}

