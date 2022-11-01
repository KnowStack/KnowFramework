package com.didiglobal.knowframework.elasticsearch.client.request.dcdr;

import com.didiglobal.knowframework.elasticsearch.client.response.dcdr.ESGetDCDRTemplateResponse;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESGetDCDRTemplateRequestBuilder extends ActionRequestBuilder<ESGetDCDRTemplateRequest, ESGetDCDRTemplateResponse, ESGetDCDRTemplateRequestBuilder> {
    public ESGetDCDRTemplateRequestBuilder(ElasticsearchClient client, ESGetDCDRTemplateAction action) {
        super(client, action, new ESGetDCDRTemplateRequest());
    }

    public ESGetDCDRTemplateRequestBuilder setName(String name) {
        request.setName(name);
        return this;
    }
}