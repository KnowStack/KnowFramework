package com.didiglobal.logi.observability;

import com.didiglobal.logi.observability.conponent.thread.ContextExecutorService;
import com.didiglobal.logi.observability.conponent.thread.ContextScheduledExecutorService;
import com.didiglobal.logi.observability.exporter.LoggingSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Observability {

    private static OpenTelemetry delegate = initOpenTelemetry();

    /**
     * @return 初始化 OpenTelemetry 对象
     */
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

    /**
     * @param instrumentationScopeName 域名
     * @return 获取给定域名对应 tracer 对象
     */
    public static Tracer getTracer(String instrumentationScopeName) {
        return delegate.getTracer(instrumentationScopeName);
    }

    /**
     * @return 获取当前 span id
     */
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

    /**
     * @return 获取当前 span 所属 trace 对应 trace id
     */
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

    /**
     * @param executor 待封装线程池
     * @return 附带 trace 上下文信息线程池
     */
    public static ExecutorService wrap(ExecutorService executor) {
        return new ContextExecutorService(executor);
    }

    /**
     * @param executor 待封装调度线程池
     * @return 附带 trace 上下文信息调度线程池
     */
    public static ScheduledExecutorService wrap(ScheduledExecutorService executor) {
        return new ContextScheduledExecutorService(executor);
    }

    public static TextMapPropagator getTextMapPropagator() {
        return delegate.getPropagators().getTextMapPropagator();
    }

}
