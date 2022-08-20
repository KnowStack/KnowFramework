package com.didiglobal.logi.observability;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.observability.common.bean.LogEvent;
import com.didiglobal.logi.observability.common.bean.Span;
import com.didiglobal.logi.observability.common.enums.LogEventType;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

public class LoggingSpanExporter implements SpanExporter {

    private static final Logger logger = Logger.getLogger(LoggingSpanExporter.class.getName());

    public static LoggingSpanExporter create() {
        return new LoggingSpanExporter();
    }

    /** @deprecated */
    @Deprecated
    public LoggingSpanExporter() {
    }

    public CompletableResultCode export(Collection<SpanData> spans) {
        for (SpanData span : spans) {
            /*
             * span info
             */
            String spanName = span.getName();
            String traceId = span.getTraceId();
            String spanId = span.getSpanId();
            SpanKind spanKind = span.getKind();
            String parentSpanId = span.getParentSpanId();
            Long startEpochNanos = span.getStartEpochNanos();
            Long endEpochNanos = span.getEndEpochNanos();
            StatusCode statusData = span.getStatus().getStatusCode();

            /*
             * tracer info
             */
            InstrumentationScopeInfo instrumentationScopeInfo = span.getInstrumentationScopeInfo();
            String tracerName = instrumentationScopeInfo.getName();
            String tracerVersion = instrumentationScopeInfo.getVersion();
            /*
             * attributes
             */
            Map<AttributeKey<?>, Object> attributes = span.getAttributes().asMap();
            logger.info(
                    JSON.toJSONString(
                            new LogEvent(
                                    LogEventType.TRACE,
                                    new Span(
                                            spanName, traceId, spanId, spanKind, parentSpanId, startEpochNanos, endEpochNanos, statusData, tracerName, tracerVersion, attributes
                                    )
                            )
                    )
            );
        }
        return CompletableResultCode.ofSuccess();
    }

    public CompletableResultCode flush() {
        CompletableResultCode resultCode = new CompletableResultCode();
        return resultCode.succeed();
    }

    public CompletableResultCode shutdown() {
        return this.flush();
    }

}
