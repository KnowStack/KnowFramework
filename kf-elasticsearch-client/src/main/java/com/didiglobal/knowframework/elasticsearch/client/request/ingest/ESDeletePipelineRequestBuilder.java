package com.didiglobal.knowframework.elasticsearch.client.request.ingest;

import com.didiglobal.knowframework.elasticsearch.client.response.ingest.ESDeletePipelineResponse;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESDeletePipelineRequestBuilder extends ActionRequestBuilder<ESDeletePipelineRequest, ESDeletePipelineResponse, ESDeletePipelineRequestBuilder> {
    public ESDeletePipelineRequestBuilder(ElasticsearchClient client, ESDeletePipelineAction action) {
        super(client, action, new ESDeletePipelineRequest());
    }

    public ESDeletePipelineRequestBuilder setPipelineId(String pipelineId) {
        request.setPipelineId(pipelineId);
        return this;
    }
}
