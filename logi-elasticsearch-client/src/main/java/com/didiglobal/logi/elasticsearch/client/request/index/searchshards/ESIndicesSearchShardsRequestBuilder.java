

package com.didiglobal.logi.elasticsearch.client.request.index.searchshards;

import com.didiglobal.logi.elasticsearch.client.request.broadcast.ESBroadcastOperationRequestBuilder;
import com.didiglobal.logi.elasticsearch.client.response.indices.searchshards.ESIndicesSearchShardsResponse;
import org.elasticsearch.client.ElasticsearchClient;


public class ESIndicesSearchShardsRequestBuilder extends ESBroadcastOperationRequestBuilder<ESIndicesSearchShardsRequest, ESIndicesSearchShardsResponse, ESIndicesSearchShardsRequestBuilder> {

    public ESIndicesSearchShardsRequestBuilder(ElasticsearchClient client, ESIndicesSearchShardsAction action) {
        super(client, action, new ESIndicesSearchShardsRequest());
    }
}
