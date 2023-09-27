package com.didiglobal.knowframework.observability.expand;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.util.NetworkUtils;
import com.didiglobal.knowframework.observability.exporter.LoggingMetricExporter;
import com.didiglobal.knowframework.observability.exporter.LoggingSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import java.time.Duration;
import java.util.List;

/**
 * @author slhu
 */
public interface OpenTelemetryExpandFactory {
    /**
     * @return 初始化 OpenTelemetry 对象
     */
    default OpenTelemetry initOpenTelemetry() {

        // Create an instance of PeriodicMetricReader and configure it
        // to export via the logging exporter
        Resource resource = this.getResource();
        MetricReader periodicReader =
                PeriodicMetricReader.builder(this.getMetricExporter())
                        .setInterval(Duration.ofMillis(Observability.getMetricExportIntervalMs()))
                        .build();
        // This will be used to create instruments
        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder().registerMetricReader(periodicReader).setResource(resource).build();
        // Tracer provider configured to export spans with SimpleSpanProcessor using
        // the logging exporter.
        SdkTracerProviderBuilder builder = SdkTracerProvider.builder().addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build());
        Observability.getExpandSpanProcessorSet().forEach(builder::addSpanProcessor);
        SdkTracerProvider tracerProvider = builder.setResource(resource).build();
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        Runtime.getRuntime().addShutdownHook(new Thread(meterProvider::close));
        return sdk;
    }

    List<SpanProcessor> getSpanProcessorList();

    default MetricExporter getMetricExporter() {
        return LoggingMetricExporter.create();
    }

    default Resource getResource() {
        return Resource.getDefault().merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, Observability.getApplicationName()))).merge(Resource.create(Attributes.of(ResourceAttributes.HOST_NAME, NetworkUtils.getHostName())));
    }
}
