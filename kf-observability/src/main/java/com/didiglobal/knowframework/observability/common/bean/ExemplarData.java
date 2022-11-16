package com.didiglobal.knowframework.observability.common.bean;

import lombok.Data;

@Data
public class ExemplarData {

    private long epochNanos;

    private String traceId;

    private String spanId;

}
