package com.didiglobal.knowframework.elasticsearch.client.response.security;

import com.didiglobal.knowframework.elasticsearch.client.model.ESActionResponse;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESDeleteSecurityUserResponse extends ESActionResponse {

    private Boolean found;


    public Boolean getFound() {
        return found;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }
}
