package com.didiglobal.knowframework.observability.common.bean;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.trace.data.SpanData;
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

    public Span(SpanData span) {
        /*
         * span info
         */
        this.spanName = span.getName();
        this.traceId = span.getTraceId();
        this.spanId = span.getSpanId();
        this.spanKind = span.getKind();
        this.parentSpanId = span.getParentSpanId();
        this.startEpochNanos = span.getStartEpochNanos();
        this.endEpochNanos = span.getEndEpochNanos();
        this.statusData = span.getStatus().getStatusCode();

        /*
         * tracer info
         */
        InstrumentationScopeInfo instrumentationScopeInfo = span.getInstrumentationScopeInfo();
        this.tracerName = instrumentationScopeInfo.getName();
        this.tracerVersion = instrumentationScopeInfo.getVersion();
        /*
         * attributes
         */
        this.attributes = span.getAttributes().asMap();
        this.spentNanos = this.endEpochNanos - this.startEpochNanos;
    }

    public Span() {

    }

}
