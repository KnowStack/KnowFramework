package com.didiglobal.logi.log.log4j2.appender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.observability.common.bean.Log;
import com.didiglobal.logi.observability.common.bean.Span;
import com.didiglobal.logi.observability.common.enums.LogEventType;
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
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

@Plugin(name = ElasticsearchAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class ElasticsearchAppender extends AbstractAppender {

    public static final String PLUGIN_NAME = "ElasticsearchAppender";

    private static final String           COMMA             = ",";
    private static final String TYPE_NAME_DEFAULT_VALUE = "type";
    private static final String THRESHOLD_DEFAULT_VALUE = "all";
    private static final Integer BUFFER_SIZE_DEFAULT_VALUE = 1000;

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

    private volatile CompletableFuture<?> availableFuture = new CompletableFuture<>();
    private volatile CompletableFuture<?> notAvailableFuture = new CompletableFuture<>();

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
            String threshold,
            Integer bufferSize) {
        super(name, filter, layout);
        this.address = address;
        this.port = port;
        this.user = user;
        this.password = password;
        this.indexName = indexName;
        this.typeName = getTypeName(typeName);
        this.threshold = getThreshold(threshold);
        this.bufferSize = getBufferSize(bufferSize);
        //初始化缓冲区
        this.buffer = new LinkedBlockingQueue<>(bufferSize);
        //初始化 elasticsearch client
        this.client = getRestHighLevelClient();
        //构建 elasticsearch 缓冲区刷写线程
        threadPool = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CustomizableThreadFactory("Log4jElasticsearchAppenderThreadPool")
        );
        threadPool.execute(new SendLogEvent2ElasticsearchRunnable());
    }

    class SendLogEvent2ElasticsearchRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
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
            this.client.close();
        } catch (IOException ex) {
            //ignore
        } finally {
            super.stop();
        }
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
            @PluginAttribute("threshold") String threshold,
            @PluginAttribute("bufferSize") int bufferSize,
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
                threshold,
                bufferSize
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
        item.put("className", event.getSource().getClassName());
        item.put("fileName", event.getSource().getFileName());
        item.put("lineNumber", event.getSource().getLineNumber());
        item.put("methodName", event.getSource().getMethodName());
        item.put("logName", event.getLoggerName());
        item.put("logLevel", event.getLevel().toString());
        item.put("logThread", event.getThreadName());
        item.put("logMills", new Date(event.getTimeMillis()));
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
                    item.put("logType", LogEventType.LOG.name());
                    putLog(log, item);
                } else if(LogEventType.TRACE.name().equals(logEventType)) {
                    Span span = jsonObject.getObject("data", Span.class);
                    item.put("logType", LogEventType.TRACE.name());
                    putSpan(span, item);
                } else if(LogEventType.METRIC.name().equals(logEventType)) {
                    // TODO：
                } else {
                    // other whise
                    item.put("message", message);
                }
            } else {
                item.put("message", message);
            }
        } catch (Exception ex) {
            // process message error
            item.put("message", message);
        }
        return item;
    }

    private void putSpan(Span span, Map<String, Object> item) {
        item.put("spanName", span.getSpanName());
        item.put("traceId", span.getTraceId());
        item.put("spanId", span.getSpanId());
        item.put("spanKind", span.getSpanKind().name());
        item.put("parentSpanId", span.getParentSpanId());
        item.put("startEpochNanos", span.getStartEpochNanos());
        item.put("endEpochNanos", span.getEndEpochNanos());
        item.put("statusData", span.getStatusData().name());
        item.put("spentNanos", span.getSpentNanos());
        item.put("tracerName", span.getTracerName());
        item.put("tracerVersion", span.getTracerVersion());
        if(MapUtils.isNotEmpty(span.getAttributes())) {
            for (Map.Entry<AttributeKey<?>, Object> entry : span.getAttributes().entrySet()) {
                AttributeKey<?> key = entry.getKey();
                Object value = entry.getValue();
                item.put(key.getKey(), value);
            }
        }
    }

    private void putLog(Log log, Map<String, Object> item) {
        item.put("tracerId", log.getTracerId());
        item.put("spanId", log.getSpanId());
        item.put("message", log.getMessage());
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

