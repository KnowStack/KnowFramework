package org.elasticsearch.plugin.nlpcn.executors;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.plugin.nlpcn.ElasticJoinExecutor;
import org.elasticsearch.plugin.nlpcn.GetIndexRequestRestListener;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.action.support.RestStatusToXContentListener;
import org.nlpcn.es4sql.query.QueryAction;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;
import org.nlpcn.es4sql.query.join.JoinRequestBuilder;

import java.util.Map;

public class ElasticDefaultRestExecutor implements RestExecutor {


    public ElasticDefaultRestExecutor() {
    }

    /**
     * Execute the ActionRequest and returns the REST response using the channel.
     */
    @Override
    public void execute(Client client, Map<String, String> params, QueryAction queryAction, RestChannel channel) throws Exception {
        SqlElasticRequestBuilder requestBuilder = queryAction.explain();
        ActionRequest request = requestBuilder.request();

        //todo: maby change to instanceof multi?
        if (requestBuilder instanceof JoinRequestBuilder) {
            ElasticJoinExecutor executor = ElasticJoinExecutor.createJoinExecutor(client, requestBuilder);
            executor.run();
            executor.sendResponse(channel);
        } else if (request instanceof SearchRequest) {
            client.search((SearchRequest) request, new RestStatusToXContentListener<SearchResponse>(channel));
        } else if (request instanceof DeleteByQueryRequest) {
            throw new UnsupportedOperationException("currently not support delete on elastic 2.x");
        } else if (request instanceof GetIndexRequest) {
            requestBuilder.getBuilder().execute(new GetIndexRequestRestListener(channel, (GetIndexRequest) request));
        } else {
            throw new Exception(String.format("Unsupported ActionRequest provided: %s", request.getClass().getName()));
        }
    }
}
