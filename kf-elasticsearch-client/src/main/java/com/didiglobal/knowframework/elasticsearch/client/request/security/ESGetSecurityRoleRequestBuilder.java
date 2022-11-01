package com.didiglobal.knowframework.elasticsearch.client.request.security;

import com.didiglobal.knowframework.elasticsearch.client.response.security.ESGetSecurityRoleResponse;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESGetSecurityRoleRequestBuilder extends ActionRequestBuilder<ESGetSecurityRoleRequest, ESGetSecurityRoleResponse, ESGetSecurityRoleRequestBuilder> {

    public ESGetSecurityRoleRequestBuilder(ElasticsearchClient client, ESGetSecurityRoleAction action) {
        super(client, action, new ESGetSecurityRoleRequest());
    }

    public ESGetSecurityRoleRequestBuilder setRoleName(String roleName) {
        request.setRoleName(roleName);
        return this;
    }
}
