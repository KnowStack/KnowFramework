//package com.didiglobal.logi.log.log4j2.appender;
//
//import com.didiglobal.logi.elasticsearch.client.ESClient;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.logging.log4j.core.*;
//import org.apache.logging.log4j.core.appender.AbstractAppender;
//import org.apache.logging.log4j.core.config.plugins.Plugin;
//import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
//import org.apache.logging.log4j.core.config.plugins.PluginElement;
//import org.apache.logging.log4j.core.config.plugins.PluginFactory;
//import org.apache.logging.log4j.core.layout.PatternLayout;
//import org.apache.logging.log4j.util.ReadOnlyStringMap;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.slf4j.MDC;
//import org.springframework.util.CollectionUtils;
//import java.io.IOException;
//import java.io.Serializable;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.NetworkInterface;
//import java.net.UnknownHostException;
//import java.util.*;
//
//@Plugin(name = NoRepeatRollingFileAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
//public class ElasticsearchAppender extends AbstractAppender {
//
//    public static final String PLUGIN_NAME = "ElasticsearchAppender";
//
//    private static final String           COMMA             = ",";
//
//    private String address;
//    private Integer port;
//    private String user;
//    private String password;
//    private String threshold;
//    /**
//     * 日志缓冲池大小,如果缓冲池大小为1则日志会被立即同步到ES中</br>
//     * 否则需要等到缓冲池Size达到bufferSize了后才会将日志刷新至ES</br>
//     * bufferSize默认初始化为1
//     */
//    private int bufferSize;
//    /**
//     * 日志缓冲数据
//     */
//    private List<Map<String, Object>> buffers = new ArrayList<>();
//    /**
//     * 操作ES集群的客户端
//     */
//    private RestHighLevelClient client;
//    /**
//     * 插入索引
//     */
//    private String index;
//
//    protected ElasticsearchAppender(String name, Filter filter, Layout<? extends Serializable> layout,
//                                    String address,
//                                    Integer port,
//                                    String index,
//                                    String user,
//                                    String password,
//                                    String threshold,
//                                    int bufferSize) {
//        super(name, filter, layout);
//        this.address = address;
//        this.port = port;
//        this.index = index;
//        this.user = user;
//        this.password = password;
//        this.threshold = threshold;
//        this.bufferSize = bufferSize;
//    }
//
//
//    private void parseLog(LogEvent event) throws JsonProcessingException, UnknownHostException {
//        ReadOnlyStringMap contextData = event.getContextData();
//        //System.out.println(LOG_LEVEL_PATTERN);
//        String applicationId = MDC.get("applicationId");
//        Map<String, Object> item = new HashMap<>();
//        item.put("className", event.getSource().getClassName());
//        item.put("fileName", event.getSource().getFileName());
//        item.put("lineNumber", event.getSource().getLineNumber());
//        item.put("methodName", event.getSource().getMethodName());
//        item.put("serverIp", getIp());
//        item.put("logName", event.getLoggerName());
//        item.put("logLevel", event.getLevel().toString());
//        item.put("logThread", event.getThreadName());
//        item.put("logMills", new Date(event.getTimeMillis()));
//        item.put("logMessage", JsonHelper.toJSONString(event.getMessage().getFormattedMessage()));
//        item.put("applicationId", applicationId);
//        //全部或者获取对应的日志级别
//        if ((threshold.equalsIgnoreCase(event.getLevel().toString()) || threshold.equalsIgnoreCase("all"))
//                && StringUtil.isNotBlank(applicationId)) {
//            buffers.add(item);
//        }
//
//    }
//
//
//    /**
//     * 将数据写入到ES中
//     */
//    private void flushBuffer() {
//        if (!CollectionUtils.isEmpty(buffers)) {
//            try {
//                bulkLoadMap(index, buffers);
//                buffers.clear();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 批量写入
//     * @param esIndex 索引
//     * @param datalist 数据
//     * @throws IOException
//     */
//    public void bulkLoadMap(String esIndex, List<Map<String, Object>> datalist) throws IOException {
//        //获取客户端
//        RestHighLevelClient client = getRestHighLevelClient();
//        BulkRequest bulkRequest = new BulkRequest();
//        for (Map<String, Object> data : datalist) {
//            Object id = data.get("sys_uuid");
//            //如果数据包含id字段，使用数据id作为文档id
//            if (id != null) {
//                bulkRequest.add(new IndexRequest(esIndex).id(id.toString()).source(data));
//            } else {//让es自动生成id
//                bulkRequest.add(new IndexRequest(esIndex).source(data));
//            }
//        }
//        client.bulk(bulkRequest, RequestOptions.DEFAULT);
//    }
//
//    /**
//     * 获取ES RestHighLevelClient客户端
//     */
//    private RestHighLevelClient getRestHighLevelClient() {
//        if (client == null) {
//            RestClientBuilder builder = RestClient.builder(new HttpHost(address, port, "http"));
//
//            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                    .setDefaultIOReactorConfig(IOReactorConfig.custom()
//                            .setSoKeepAlive(true)
//                            .build())).setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
//                    .setConnectTimeout(40000)
//                    .setSocketTimeout(40000));
//
//            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
//            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
//            client = new RestHighLevelClient(builder);
//        }
//        return client;
//    }
//
//    /**
//     * 获取ip
//     * @return
//     * @throws UnknownHostException
//     */
//    private String getIp() throws UnknownHostException {
//        try {
//            InetAddress candidateAddress = null;
//            // 遍历所有的网络接口
//            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
//                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
//                // 在所有的接口下再遍历IP
//                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
//                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
//                    // 排除loopback类型地址
//                    if (!inetAddr.isLoopbackAddress()) {
//                        if (inetAddr.isSiteLocalAddress()) {
//                            // 如果是site-local地址，就是它了
//                            return inetAddr.getHostAddress();
//                        } else if (candidateAddress == null) {
//                            // site-local类型的地址未被发现，先记录候选地址
//                            candidateAddress = inetAddr;
//                        }
//                    }
//                }
//            }
//            if (candidateAddress != null) {
//                return candidateAddress.getHostAddress();
//            }
//            // 如果没有发现 non-loopback地址.只能用最次选的方案
//            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
//            if (jdkSuppliedAddress == null) {
//                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
//            }
//            return jdkSuppliedAddress.getHostAddress();
//        } catch (Exception e) {
//            UnknownHostException unknownHostException = new UnknownHostException(
//                    "Failed to determine LAN address: " + e);
//            unknownHostException.initCause(e);
//            throw unknownHostException;
//        }
//    }
//    /**
//     * 类结束之前调用方法
//     */
//    @Override
//    public void stop() {
//        if (buffers.size() > 0) {
//            flushBuffer();
//        }
//        try {
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        super.stop();
//
//    }
//
//    @Override
//    public void append(LogEvent event) {
//        try {
//            parseLog(event);
//            if (buffers.size() >= (bufferSize == 0 ? 50 : bufferSize)) {
//                flushBuffer();
//            }
//        } catch (JsonProcessingException | UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @PluginFactory
//    public static ElasticsearchAppender createAppender(
//            @PluginAttribute("name") String name,
//                                                       @PluginAttribute("address") String address,
//                                                       @PluginAttribute("port") Integer port,
//                                                       @PluginAttribute("index") String index,
//                                                       @PluginAttribute("user") String user,
//                                                       @PluginAttribute("password") String password,
//                                                       @PluginAttribute("threshold") String threshold,
//                                                       @PluginAttribute("bufferSize") int bufferSize,
//                                                       @PluginElement("Filter") final Filter filter,
//                                                       @PluginElement("Layout") Layout<? extends Serializable> layout
//    ) {
//        if (name == null) {
//            LOGGER.error("No name provided for MyCustomAppenderImpl");
//            return null;
//        }
//        if (layout == null) {
//            layout = PatternLayout.createDefaultLayout();
//        }
//        return new ElasticsearchAppender(name, filter, layout, address, port, index, user, password, threshold, bufferSize);
//    }
//
//    private ESClient buildEsClient(String address, String password, String clusterName) {
//        if (StringUtils.isBlank(address)) {
//            return null;
//        }
//        ESClient esClient = new ESClient();
//        try {
//            String[] httpAddressArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(address, COMMA);
//            TransportAddress[] transportAddresses = new TransportAddress[httpAddressArray.length];
//
//            for (int i = 0; i < httpAddressArray.length; ++i) {
//                String[] httpAddressAndPortArray = StringUtils
//                        .splitByWholeSeparatorPreserveAllTokens(httpAddressArray[i], ":");
//                if (httpAddressAndPortArray != null && httpAddressAndPortArray.length == 2) {
//                    transportAddresses[i] = new InetSocketTransportAddress(
//                            new InetSocketAddress(httpAddressAndPortArray[0], Integer.valueOf(httpAddressAndPortArray[1])));
//                }
//            }
//            esClient.addTransportAddresses(transportAddresses);
//            if (StringUtils.isNotBlank(clusterName)) {
//                esClient.setClusterName(clusterName);
//            }
//            if(StringUtils.isNotBlank(password)){
//                esClient.setPassword(password);
//            }
//            // 配置http超时
//            esClient.setRequestConfigCallback(builder -> builder.setConnectTimeout(10000).setSocketTimeout(120000)
//                    .setConnectionRequestTimeout(120000));
//            esClient.start();
//            return esClient;
//        } catch (Exception e) {
//            esClient.close();
//
//            LOGGER.error("class=ESUpdateClient||method=buildEsClient||errMsg={}||address={}", e.getMessage(), address,
//                    e);
//            return null;
//        }
//    }
//
//}
//
