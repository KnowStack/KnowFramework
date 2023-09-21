package com.didiglobal.knowframework.observability.common;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.util.NetworkUtils;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import java.util.List;

public interface OpenTelemetryExpandFactory {
    List<SpanProcessor> getSpanProcessorList();

    default Resource getResource() {
        return Resource.getDefault()
                .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, Observability.getApplicationName())))
                .merge(Resource.create(Attributes.of(ResourceAttributes.HOST_NAME, NetworkUtils.getHostName())));
    }
}
