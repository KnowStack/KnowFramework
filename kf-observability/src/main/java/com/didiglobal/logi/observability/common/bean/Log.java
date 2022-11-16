package com.didiglobal.logi.observability.common.bean;

import lombok.Data;

@Data
public class Log {

    private String tracerId;
    private String spanId;
    private String message;

    public Log(String tracerId, String spanId, String message) {
        this.tracerId = tracerId;
        this.spanId = spanId;
        this.message = message;
    }

    public Log() {
    }

}
