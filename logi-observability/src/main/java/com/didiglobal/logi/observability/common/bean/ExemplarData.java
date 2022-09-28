package com.didiglobal.logi.observability.common.bean;

import lombok.Data;

@Data
public class ExemplarData {

    private long epochNanos;

    private String traceId;

    private String spanId;

}
