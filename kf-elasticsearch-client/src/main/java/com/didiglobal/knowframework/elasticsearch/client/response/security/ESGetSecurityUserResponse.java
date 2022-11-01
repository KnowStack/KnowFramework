package com.didiglobal.knowframework.elasticsearch.client.response.security;

import com.didiglobal.knowframework.elasticsearch.client.request.security.SecurityUser;
import com.didiglobal.knowframework.elasticsearch.client.response.ESAcknowledgedResponse;

import java.util.Map;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESGetSecurityUserResponse extends ESAcknowledgedResponse {

    private Map<String, SecurityUser> userMap;


    public Map<String, SecurityUser> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, SecurityUser> userMap) {
        this.userMap = userMap;
    }
}
