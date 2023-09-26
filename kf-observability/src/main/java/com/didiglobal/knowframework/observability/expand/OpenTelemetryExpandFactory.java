package com.didiglobal.knowframework.observability.expand;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.util.NetworkUtils;
import com.didiglobal.knowframework.observability.exporter.LoggingMetricExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import java.util.List;

/**
 * @author slhu
 */
public interface OpenTelemetryExpandFactory {
    /**
     * @return 初始化 OpenTelemetry 对象
     */
    default OpenTelemetry initOpenTelemetry() {
        return Observability.initOpenTelemetry();
    }

    List<SpanProcessor> getSpanProcessorList();

    default MetricExporter getMetricExporter() {
        return LoggingMetricExporter.create();
    }

    default Resource getResource() {
        return Resource.getDefault().merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, Observability.getApplicationName()))).merge(Resource.create(Attributes.of(ResourceAttributes.HOST_NAME, NetworkUtils.getHostName())));
    }
}
