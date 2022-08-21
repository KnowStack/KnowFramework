package com.didiglobal.logi.observability;

import com.didiglobal.logi.observability.exporter.LoggingSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.apache.commons.lang3.StringUtils;

public class Observability {

    private static OpenTelemetry delegate = initOpenTelemetry();

    private static OpenTelemetry initOpenTelemetry() {
        // Tracer provider configured to export spans with SimpleSpanProcessor using
        // the logging exporter.
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                        .build();
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }

    public static Tracer getTracer(String instrumentationScopeName) {
        return delegate.getTracer(instrumentationScopeName);
    }

    public static String getCurrentSpanId() {
        Span span = Span.current();
        if(Span.getInvalid() != span && span.getSpanContext().isValid()) {
            String spanId = span.getSpanContext().getSpanId();
            return spanId;
        } else {
            //当前上下文不存在于任何span中
            return StringUtils.EMPTY;
        }
    }

    public static String getCurrentTraceId() {
        Span span = Span.current();
        if(Span.getInvalid() != span && span.getSpanContext().isValid()) {
            String tracerId = span.getSpanContext().getTraceId();
            return tracerId;
        } else {
            //当前上下文不存在于任何span中
            return StringUtils.EMPTY;
        }
    }

}
