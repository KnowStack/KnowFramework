package com.didiglobal.logi.observability.common.bean;

import io.opentelemetry.api.common.AttributeKey;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MetricPoint {

    private String instrumentationScopeInfoName;
    private String metricName;
    private String metricDescription;
    private String metricUnit;
    private String metricType;
    private Long metricEpochNanos;
    private Object metricValue;
    private Map<AttributeKey<?>, Object> attributes;
    private List<ExemplarData> exemplarDataList;

}
