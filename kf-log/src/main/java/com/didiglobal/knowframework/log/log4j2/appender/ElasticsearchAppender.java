package com.didiglobal.knowframework.log.log4j2.appender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didiglobal.knowframework.observability.common.bean.Log;
import com.didiglobal.knowframework.observability.common.bean.Metric;
import com.didiglobal.knowframework.observability.common.bean.Span;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import com.didiglobal.knowframework.observability.common.enums.LogEventType;
import io.opentelemetry.api.common.AttributeKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
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
    private static final Integer BUFFER_SIZE_DEFAULT_VALUE = 100;
    private static final Integer NUMBER_OF_SHARDS_DEFAULT_VALUE = 1;
    private static final Integer NUMBER_OF_REPLICAS_DEFAULT_VALUE = 1;
    private static final Integer LOG_EXPIRE_DEFAULT_VALUE = 7;
    private static final Integer REQUEST_TIME_OUT_MILLIS_DEFAULT_VALUE = 3000;

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
     * 日志缓冲数据
     */
    private BlockingQueue<Map<String, Object>> buffer;
    /**
     * log event 刷写线程
     */
    private ExecutorService threadPool;
    /**
     * 日志过期阈值，单位：日
     */
    private Integer logExpire;
    /**
     * 扩展字段类
     */
    private String extendsMappingClass;
    /**
     * elasticsearch 请求超时时间 单位：毫秒
     */
    private Integer requestTimeoutMillis;
    /**
     * http 请求前缀
     */
    private String requestUrlPrefix;
    /**
     * buffer 满时是否丢弃日志
     */
    private Boolean discardWhenBufferIsFull;

    private volatile CompletableFuture<?> availableFuture = new CompletableFuture<>();
    private volatile CompletableFuture<?> notAvailableFuture = new CompletableFuture<>();

    private volatile Boolean sendLogEvent2ElasticsearchRunnableSwitch;
    private volatile Boolean elasticsearchLogCleanRunnableSwitch;

    private HttpUtils httpUtils;

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
            Integer logExpire,
            String extendsMappingClass,
            Integer requestTimeoutMillis,
            Boolean discardWhenBufferIsFull
    ) {
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
        this.extendsMappingClass = extendsMappingClass;
        this.requestTimeoutMillis = getRequestTimeoutMillis(requestTimeoutMillis);
        this.discardWhenBufferIsFull = getDiscardWhenBufferIsFull(discardWhenBufferIsFull);
        //初始化缓冲区
        this.buffer = new LinkedBlockingQueue<>(bufferSize);
        this.httpUtils = HttpUtils.getInstance(requestTimeoutMillis);
        //初始化 requestUrlPrefix
        this.requestUrlPrefix = String.format("http://%s:%d", this.address, this.port);
        //校验 index 是否已创建，如未创建，则进行创建 创建 时 采用 给 定 elasticsearch mappings.
        try {
            verifyAndCreateElasticsearchIndexIfNotExists();
        } catch (Exception ex) {
            //索引创建失败，elasticsearch appender 实例化流程终止
            LOGGER.error(
                    String.format(
                            "class=%s||method=%s||msg=%s",
                            this.getClass().getName(),
                            "ElasticsearchAppender()",
                            "invoke function verifyAndCreateElasticsearchIndexIfNotExists() failed, cause by:" + ex.getMessage()
                    )
            );
            return;
        }
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
            threadPool.execute(new ElasticsearchLogCleanRunnable(this.indexName, this.requestUrlPrefix, this.user, this.password, this.httpUtils));
        }
    }

    private Boolean getDiscardWhenBufferIsFull(Boolean discardWhenBufferIsFull) {
        if(null == discardWhenBufferIsFull) {
            return Boolean.TRUE;
        } else {
            return discardWhenBufferIsFull;
        }
    }

    private Integer getRequestTimeoutMillis(Integer requestTimeoutMillis) {
        if(null == requestTimeoutMillis || 0 >= requestTimeoutMillis) {
            return REQUEST_TIME_OUT_MILLIS_DEFAULT_VALUE;
        } else {
            return requestTimeoutMillis;
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

    private void verifyAndCreateElasticsearchIndexIfNotExists() throws Exception {
        boolean indexExists = elasticsearchIndexExists();
        if(!indexExists) {
            createElasticsearchIndex();
        }
    }

    private void createElasticsearchIndex() throws Exception {
        String paramString = getIndexCreateParam();
        String url = this.requestUrlPrefix + "/" + indexName;
        this.httpUtils.putForString(url, paramString, null, this.user, this.password);
        //此处，由于可能存在并发创建索引情况，但并发创建时，仅一个将创建索引成功，其他都将创建失败，因而无须对 response 进行进一步判断
    }

    private String getIndexCreateParam() {
        JSONObject index = new JSONObject();
        index.put("number_of_shards", this.numberOfShards);
        index.put("number_of_replicas", this.numberOfReplicas);
        JSONObject settings = new JSONObject();
        settings.put("index", index);
        JSONObject indexCreateRequestParam = new JSONObject();
        indexCreateRequestParam.put("settings", settings);
        Map<String, String> fieldSchemas = getFieldSchemas();
        JSONObject properties = new JSONObject();
        for (Map.Entry<String, String> fieldSchema : fieldSchemas.entrySet()) {
            String fieldName = fieldSchema.getKey();
            String fieldType = fieldSchema.getValue();
            JSONObject field = new JSONObject();
            field.put("type", fieldType);
            properties.put(fieldName, field);
        }
        JSONObject mappings = new JSONObject();
        mappings.put("properties", properties);
        indexCreateRequestParam.put("mappings", mappings);
        return indexCreateRequestParam.toJSONString();
    }

    private Map<String, String> getFieldSchemas() {
        //1.加载默认字段集
        Map<String, String> fieldSchemas = getDefaultFieldSchemas();
        //2.加载扩展字段集
        if(extendsMappingsConfigued()) {//配置扩展字段集
            fieldSchemas.putAll(getExtendsFieldSchemas());
        }
        return fieldSchemas;
    }

    private Map<String, String> getExtendsFieldSchemas() {
        ExtendsElasticsearchMappings extendsElasticsearchMappings = getExtendsElasticsearchMappings();
        if(null != extendsElasticsearchMappings && MapUtils.isNotEmpty(extendsElasticsearchMappings.getExtendsElasticsearchMappings())) {
            return extendsElasticsearchMappings.getExtendsElasticsearchMappings();
        }
        return MapUtils.EMPTY_MAP;
    }

    private Map<String, String> getDefaultFieldSchemas() {
        Map<String, String> defaultFieldSchemas = new HashMap<>();
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_SQL_STATEMENT, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_SQL_TYPE, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_HTTP_METHOD, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_COMPONENT, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_HTTP_URL, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_HTTP_SCHEMA, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_HTTP_HOST, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_HTTP_TARGET, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_JOB_CLASS_NAME, "keyword");
        defaultFieldSchemas.put(Constant.ATTRIBUTE_KEY_TASK_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_CLASS_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_FILE_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_LOG_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_METHOD_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_SPAN_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_TRACER_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_END_EPOCH_NANOS, "long");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_LINE_NUMBER, "long");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_LOG_LEVEL, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_LOG_MILLS, "date");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_LOG_THREAD, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_LOG_TYPE, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_MESSAGE, "text");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_PARENT_SPAN_ID, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_SPAN_ID, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_SPAN_KIND, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_SPENT_NANOS, "long");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_START_EPOCH_NANOS, "long");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_STATUS_DATA, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_TRACE_ID, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_TRACER_VERSION, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_DISK_PATH, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_DEVICE_NAME, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_METRIC_NAME, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_INSTRUMENTATION_SCOPE_INFO_NAME, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_METRIC_DESCRIPTION, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_METRIC_UNIT, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_METRIC_TYPE, "keyword");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_METRIC_EPOCH_NANOS, "date");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_METRIC_VALUE, "double");
        defaultFieldSchemas.put(Constant.METRIC_FIELD_NAME_EXEMPLAR_DATA, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_APPLICATION_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_HOST_NAME, "keyword");
        defaultFieldSchemas.put(Constant.LOG_FIELD_NAME_IP, "keyword");
        return defaultFieldSchemas;
    }

    private boolean extendsMappingsConfigued() {
        return StringUtils.isNotBlank(this.extendsMappingClass);
    }

    private ExtendsElasticsearchMappings getExtendsElasticsearchMappings() {
        try {
            Class clazz = Class.forName(this.extendsMappingClass);
            Object instance = clazz.newInstance();
            return (ExtendsElasticsearchMappings) instance;
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean elasticsearchIndexExists() {
        String url = this.requestUrlPrefix + "/_cat/indices/" + indexName;
        try {
            this.httpUtils.get(url, null, this.user, this.password);
            return true;
        } catch (Exception ex) {
            return false;
        }
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
        private String requestUrlPrefix;
        private String userName;
        private String password;
        private HttpUtils httpUtils;

        public ElasticsearchLogCleanRunnable(String indexName, String requestUrlPrefix, String userName, String password, HttpUtils httpUtils) {
            this.indexName = indexName;
            this.requestUrlPrefix = requestUrlPrefix;
            this.userName = userName;
            this.password = password;
            this.httpUtils = httpUtils;
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
                    LOGGER.error(
                            String.format(
                                    "class=%s||method=%s||msg=%s",
                                    this.getClass().getName(),
                                    "run()",
                                    String.format(
                                            "invoke function deleteByCreateTime(%d) failed, cause by:%s",
                                            System.currentTimeMillis() - logExpire * 24 * 3600 * 1000,
                                            ex.getMessage()
                                    )
                            )
                    );
                }
            }
        }

        private void deleteByCreateTime(Long createTime) throws Exception {
            String paramString = getIndexDeleteParam(createTime);
            String url = this.requestUrlPrefix + "/" + indexName + "/_delete_by_query";
            this.httpUtils.postForString(url, paramString, null, this.userName, this.password);
        }

        private String getIndexDeleteParam(Long createTime) {
            JSONObject field = new JSONObject();
            field.put("lte", dateFormat.format(createTime));
            JSONObject range = new JSONObject();
            range.put(Constant.LOG_FIELD_NAME_LOG_MILLS, field);
            JSONObject query = new JSONObject();
            query.put("range", range);
            JSONObject indexDeleteParam = new JSONObject();
            indexDeleteParam.put("query", query);
            return indexDeleteParam.toJSONString();
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
            closeClient();
        } catch (Exception ex) {
            //ignore
        } finally {
            super.stop();
        }
    }

    private void closeClient() {
        //TODO：http client 复用以后须关闭 在此

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
            @PluginAttribute("extendsMappingClass") String extendsMappingClass,
            @PluginAttribute("requestTimeoutMillis") int requestTimeoutMillis,
            @PluginAttribute("discardWhenBufferIsFull") boolean discardWhenBufferIsFull,
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
                logExpire,
                extendsMappingClass,
                requestTimeoutMillis,
                discardWhenBufferIsFull
        );
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
            while (true) {
                boolean successful = buffer.offer(element);
                if(!successful) {// the buffer is full
                    if(this.discardWhenBufferIsFull) {//写入失败丢弃
                        break;
                    } else {
                        resetUnavailable();
                        try {
                            availableFuture.get(50, TimeUnit.MILLISECONDS);
                        } catch (Exception ex) {
                            //ignore
                        }
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
                    Metric metric = jsonObject.getObject("data", Metric.class);
                    item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.METRIC.name());
                    putMetric(metric, item);
                } else {
                    // other whise
                    item.put(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.LOG.name());
                    item.put(Constant.LOG_FIELD_NAME_MESSAGE, message);
                }
                String hostName = jsonObject.getObject("hostName", String.class);
                String ip = jsonObject.getObject("ip", String.class);
                String applicationName = jsonObject.getObject("applicationName", String.class);
                item.put(Constant.LOG_FIELD_NAME_APPLICATION_NAME, applicationName);
                item.put(Constant.LOG_FIELD_NAME_HOST_NAME, hostName);
                item.put(Constant.LOG_FIELD_NAME_IP, ip);
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

    private void putMetric(Metric metric, Map<String, Object> item) {
        item.put(Constant.METRIC_FIELD_NAME_METRIC_NAME, metric.getMetricName());
        item.put(Constant.METRIC_FIELD_NAME_INSTRUMENTATION_SCOPE_INFO_NAME, metric.getInstrumentationScopeInfoName());
        item.put(Constant.METRIC_FIELD_NAME_METRIC_DESCRIPTION, metric.getMetricDescription());
        item.put(Constant.METRIC_FIELD_NAME_METRIC_UNIT, metric.getMetricUnit());
        item.put(Constant.METRIC_FIELD_NAME_METRIC_TYPE, metric.getMetricType());
        item.put(Constant.METRIC_FIELD_NAME_METRIC_EPOCH_NANOS, new Date(TimeUnit.NANOSECONDS.toMillis(metric.getMetricEpochNanos())));
        item.put(Constant.METRIC_FIELD_NAME_METRIC_VALUE, metric.getMetricValue());
        if(MapUtils.isNotEmpty(metric.getAttributes())) {
            for (Map.Entry<AttributeKey<?>, Object> entry : metric.getAttributes().entrySet()) {
                AttributeKey<?> key = entry.getKey();
                Object value = entry.getValue();
                item.put(key.getKey(), value);
            }
        }
        if(CollectionUtils.isNotEmpty(metric.getExemplarDataList())) {
            item.put(Constant.METRIC_FIELD_NAME_EXEMPLAR_DATA, JSON.toJSONString(metric.getExemplarDataList()));
        }
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
        List<Map<String, Object>> elementList = new ArrayList<>();
        Integer bulkRequestSize = 0;
        for (int i = 0; i < buffer.size(); i++) {
            Map<String, Object> element = null;
            try {
                element = buffer.poll(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if(null != element) {
                if(!availableFuture.isDone()) {
                    availableFuture.complete(null);
                }
                elementList.add(element);
                bulkRequestSize++;
            } else {
                break;
            }
        }
        if(bulkRequestSize > 0) {
            try {
                batchInsert(elementList);
            } catch (Exception ex) {
                //ignore
                LOGGER.error(
                        String.format(
                                "class=%s||method=%s||msg=%s",
                                this.getClass().getName(),
                                "flushBuffer()",
                                String.format(
                                        "invoke function batchInsert() failed, cause by:%s",
                                        ex.getMessage()
                                )
                        )
                );
            }
        }
    }

    public void batchInsert(List<Map<String, Object>> elementList) throws Exception {
        String paramString = getBulkRequestParam(elementList);
        String url = this.requestUrlPrefix + "/_bulk";
        String response = this.httpUtils.postForString(url, paramString, null, user, password);
        if(StringUtils.isBlank(response)) {
            throw new Exception(
                    "response is null"
            );
        }
        Boolean errorsExists = Boolean.TRUE;
        try {
            JSONObject responseObject = JSON.parseObject(response);
            errorsExists = responseObject.getBoolean("errors");
        } catch (Exception ex) {
            throw new Exception(
                    "parse response failed, response is:" + response
            );
        }
        if(errorsExists.equals(Boolean.TRUE)) {
            throw new Exception(
                    "insert into elasticsearch failed"
            );
        }
    }

    private String getBulkRequestParam(List<Map<String, Object>> elementList) {
        StringBuilder bulkRequestBody = new StringBuilder();
        for (Map<String, Object> element : elementList) {
            String actionMetaData = String.format("{\"index\":{\"_index\":\"%s\"}}%n", this.indexName);
            String content = JSON.toJSONString(element, SerializerFeature.WriteNullStringAsEmpty);
            bulkRequestBody.append(actionMetaData);
            bulkRequestBody.append(content);
            bulkRequestBody.append("\n");
        }
        return bulkRequestBody.toString();
    }

}

