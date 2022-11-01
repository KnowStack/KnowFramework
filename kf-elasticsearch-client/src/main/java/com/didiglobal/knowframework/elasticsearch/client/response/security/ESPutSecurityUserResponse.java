package com.didiglobal.knowframework.elasticsearch.client.response.security;

import com.alibaba.fastjson.annotation.JSONField;
import com.didiglobal.knowframework.elasticsearch.client.model.ESActionResponse;

/**
 * author weizijun
 * date：2019-07-11
 */
public class ESPutSecurityUserResponse extends ESActionResponse {

    @JSONField(name = "created")
    private Boolean created;

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }
}
