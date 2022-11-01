package com.didiglobal.knowframework.elasticsearch.client.response;

import com.didiglobal.knowframework.elasticsearch.client.model.ESActionResponse;

public class ESAcknowledgedResponse extends ESActionResponse {
    private Boolean acknowledged;

    public Boolean getAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
}
