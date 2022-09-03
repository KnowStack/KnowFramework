package com.didiglobal.logi.job.extend.impl;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.job.LogIJobProperties;
import com.didiglobal.logi.job.extend.JobLogFetcherExtend;
import com.didiglobal.logi.observability.common.constant.Constant;
import com.didiglobal.logi.observability.common.enums.LogEventType;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component("defaultJobLogFetcherExtendImpl")
public class DefaultJobLogFetcherExtendImpl implements JobLogFetcherExtend {

    private LogIJobProperties logIJobProperties;

    /**
     * 操作ES集群的客户端
     */
    private RestHighLevelClient client;

    @Autowired
    public DefaultJobLogFetcherExtendImpl(LogIJobProperties logIJobProperties) {
        this.logIJobProperties = logIJobProperties;
        this.client = getRestHighLevelClient();
    }

    @Override
    public List<String> getLogsByTraceIdFromExternalSystem(String traceId) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(logIJobProperties.getElasticsearchIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder = QueryBuilders.boolQuery().filter(
                QueryBuilders.termQuery(Constant.LOG_FIELD_NAME_TRACE_ID, traceId)
        ).filter(
                QueryBuilders.termQuery(Constant.LOG_FIELD_NAME_LOG_TYPE, LogEventType.LOG.name())
        );
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = this.client.search(request, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            Iterator<SearchHit> iterator = searchHits.iterator();
            List<String> logs = new ArrayList<>(searchHits.getHits().length);
            while (iterator.hasNext()) {
                SearchHit searchHit = iterator.next();
                logs.add(searchHit.getSourceAsString());
            }
            return logs;
        } catch (IOException ex) {
            throw new IOException(
                    String.format(
                            "从elasticsearch%s加载traceId:%s相关日志失败，",
                            JSON.toJSONString(logIJobProperties),
                            traceId
                    ),
                    ex
            );
        }
    }

    /**
     * 获取ES RestHighLevelClient客户端
     */
    private RestHighLevelClient getRestHighLevelClient() {
        if (client == null) {
            RestClientBuilder builder = RestClient.builder(new HttpHost(logIJobProperties.getElasticsearchAddress(), logIJobProperties.getElasticsearchPort(), "http"));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                    .setDefaultIOReactorConfig(IOReactorConfig.custom()
                            .setSoKeepAlive(true)
                            .build())).setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                    .setConnectTimeout(40000)
                    .setSocketTimeout(40000));

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(logIJobProperties.getElasticsearchUser(), logIJobProperties.getElasticsearchPassword()));
            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
            client = new RestHighLevelClient(builder);
        }
        return client;
    }

}
