package com.didiglobal.logi.elasticsearch.client;

import com.didiglobal.logi.elasticsearch.client.model.*;
import com.didiglobal.logi.elasticsearch.client.model.exception.ExceptionFactory;
import com.didiglobal.logi.elasticsearch.client.response.cluster.ESClusterVersionResponse;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.*;
import org.elasticsearch.client.*;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ESClient extends ESAbstractClient {
    protected static final ILog logger = LogFactory.getLog(ESClient.class);

    private List<TransportAddress> tas = new ArrayList<>();

    private RestClient restClient;
    private List<Header> headers = new ArrayList<>();
    private String uriPrefix = null;

    public static final String DEFAULT_ES_VERSION = "2.3.3";

    private static final String DEFAULT_ES_CLUSTER_NAME= "elasticsearch";

    private String esVersion;

    private String clusterName;
    /**
     * REST请求配置文件
     */
    private RestClientBuilder.RequestConfigCallback requestConfigCallback = null;

    private final AtomicReference<Boolean> running;

    private String password;

    public ESClient(String clusterName, String version) {
        this.clusterName = clusterName;
        this.esVersion = version;
        running = new AtomicReference<>(false);
    }

    public ESClient() {
        this(DEFAULT_ES_CLUSTER_NAME, DEFAULT_ES_VERSION);
    }

    public ESClient addTransportAddress(TransportAddress transportAddress) {
        tas.add(transportAddress);
        return this;
    }

    public ESClient addTransportAddresses(TransportAddress... transportAddress) {
        for(TransportAddress ta : transportAddress) {
            addTransportAddress(ta);
        }
        return this;
    }

    /**
     * String的格式必须是 ip:port,ip:port
     * @param addresses **.**.**.**:port,**.**.**.**:port  格式
     * @return 链接
     */
    public ESClient addTransportAddresses(String addresses) {
        try {

            for (String addr : addresses.split(",")) {
                String[] addrArr = addr.split(":");
                addTransportAddress(
                    new InetSocketTransportAddress(new InetSocketAddress(addrArr[0], Integer.valueOf(addrArr[1]))));
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("addresses format(ip:port,ip:port) error: " + addresses);
        }
        return this;
    }

    @Deprecated
    public ESClient setHeader(Header header) {
        this.headers.add(header);
        return this;
    }

    public ESClient addHeader(Header header) {
        this.headers.add(header);
        return this;
    }

    public ESClient setHeaders(List<Header> headers) {
        this.headers = headers;
        return this;
    }

    public ESClient setUriPrefix(String prefix) {
        this.uriPrefix = prefix;
        return this;
    }

    public ESClient setRequestConfigCallback(RestClientBuilder.RequestConfigCallback requestConfigCallback) {
        this.requestConfigCallback = requestConfigCallback;
        return this;
    }

    public void start() {
        reset();

        // 获取连接集群的版本信息
        try {
            if(!"gateway".equals(clusterName)) {
                ESClusterVersionResponse versionResponse = this.admin().cluster().prepareVersion().execute().actionGet(10,
                        TimeUnit.SECONDS);
                esVersion = versionResponse.getVersion().getNumber();
            }
        } catch (Throwable t) {
            esVersion = DEFAULT_ES_VERSION;
        }
    }

    private void reset() {
        logger.info("reset client, cluster=" + clusterName);
        
        ArrayList<HttpHost> hosts = Lists.newArrayList();

        for (TransportAddress ta : tas) {
            hosts.add(new HttpHost(ta.getAddress(), ta.getPort()));
        }

        HttpHost[] hostArr = new HttpHost[hosts.size()];
        List<Header> defaultHeaders = new ArrayList<>();
        if (password != null) {
            String encode = Base64.getEncoder().encodeToString(String.format("%s", password).getBytes(StandardCharsets.UTF_8));
            Header header = new BasicHeader("Authorization", "Basic " + encode);
            defaultHeaders.add(header);
        }

        // 如果配置了HTTP客户端超时配置
        if (requestConfigCallback != null) {
            restClient = RestClient.builder(hosts.toArray(hostArr))
                    .setRequestConfigCallback(requestConfigCallback)
                    .setMaxRetryTimeoutMillis(120000)
                    .setDefaultHeaders(defaultHeaders)
                    .build();
        } else {
            restClient = RestClient.builder(hosts.toArray(hostArr))
                    .setDefaultHeaders(defaultHeaders)
                    .build();
        }

        running.set(true);
    }

    @Override
    protected <Request extends ActionRequest,
               Response extends ActionResponse,
               RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>>
    void doExecute(Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {

        try {
            if (running.get().equals(false)) {
                throw new IllegalStateException("client not running");
            }

            ESActionRequest req = (ESActionRequest) request;
            RestRequest rr = req.buildRequest(headers);
            rr.addEndpointPrefix(uriPrefix);

            // System.out.println("request:" + rr.toString());

            org.elasticsearch.client.Request clientRequest = rr.buildRequest();

            try {
                sendRequest(req, clientRequest, listener);
            } catch (Throwable t) {
                boolean isReset = false;
                if (restClient != null && !restClient.isRunning()) {
                    if (running.compareAndSet(true, false)) {
                        restClient.close();
                        reset();
                        sendRequest(req, clientRequest, listener);
                        isReset = true;
                    }
                }

                if (!isReset) {
                    listener.onFailure(ExceptionFactory.translate(t));
                }
            }
        } catch (Throwable t) {
            listener.onFailure(ExceptionFactory.translate(t));
        }
    }

    private <Response extends ActionResponse> void sendRequest(ESActionRequest req, Request request, ActionListener<Response> listener) {
        restClient.performRequestAsync(request, new ResponseListener() {
            @Override
            public void onSuccess(org.elasticsearch.client.Response response) {
                try {
                    if (req.checkResponse(response)) {
                        RestResponse restResponse = new RestResponse(getEsVersion(), response);
                        ESActionResponse tr = req.buildResponse(restResponse);

                        listener.onResponse((Response) tr);
                    } else {
                        throw new ResponseException(response);
                    }
                } catch (Exception e) {
                    listener.onFailure(e);
                }

            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(ExceptionFactory.translate(e));
            }
        });
    }


    @Override
    public void close() {
        try {
            restClient.close();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    public String getEsVersion() {
        return esVersion;
    }

    public void setEsVersion(String esVersion) {
        this.esVersion = esVersion;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
