package com.didiglobal.knowframework.elasticsearch.client.request.security;

import com.didiglobal.knowframework.elasticsearch.client.response.security.ESGetSecurityUserResponse;
import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESGetSecurityUserAction extends Action<ESGetSecurityUserRequest, ESGetSecurityUserResponse, ESGetSecurityUserRequestBuilder> {
    public static final ESGetSecurityUserAction INSTANCE = new ESGetSecurityUserAction();
    public static final String NAME = "cluster:admin/security/user/get";

    private ESGetSecurityUserAction() {
        super(NAME);
    }

    @Override
    public ESGetSecurityUserResponse newResponse() {
        return new ESGetSecurityUserResponse();
    }

    @Override
    public ESGetSecurityUserRequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new ESGetSecurityUserRequestBuilder(client, this);
    }
}
