package com.didiglobal.knowframework.elasticsearch.client.response.security;

import com.didiglobal.knowframework.elasticsearch.client.request.security.SecurityRole;
import com.didiglobal.knowframework.elasticsearch.client.response.ESAcknowledgedResponse;

import java.util.Map;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESGetSecurityRoleResponse extends ESAcknowledgedResponse {

    private Map<String, SecurityRole> roleMap;

    public Map<String, SecurityRole> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<String, SecurityRole> roleMap) {
        this.roleMap = roleMap;
    }
}
