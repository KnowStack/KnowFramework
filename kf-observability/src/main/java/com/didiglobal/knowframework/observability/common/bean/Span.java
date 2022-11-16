package com.didiglobal.knowframework.observability.common.bean;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import lombok.Data;

import java.util.Map;

@Data
public class Span {

    private String spanName;
    private String traceId;
    private String spanId;
    private SpanKind spanKind;
    private String parentSpanId;
    private Long startEpochNanos;
    private Long endEpochNanos;
    private StatusCode statusData;
    private Long spentNanos;

    /*
     * tracer info
     */
    private String tracerName;
    private String tracerVersion;
    /*
     * attributes
     */
    private Map<AttributeKey<?>, Object> attributes;

    public Span(String spanName, String traceId, String spanId, SpanKind spanKind, String parentSpanId, Long startEpochNanos, Long endEpochNanos, StatusCode statusData, String tracerName, String tracerVersion, Map<AttributeKey<?>, Object> attributes) {
        this.spanName = spanName;
        this.traceId = traceId;
        this.spanId = spanId;
        this.spanKind = spanKind;
        this.parentSpanId = parentSpanId;
        this.startEpochNanos = startEpochNanos;
        this.endEpochNanos = endEpochNanos;
        this.statusData = statusData;
        this.tracerName = tracerName;
        this.tracerVersion = tracerVersion;
        this.attributes = attributes;
        this.spentNanos = this.endEpochNanos - this.startEpochNanos;
    }

    public Span() {

    }

}
