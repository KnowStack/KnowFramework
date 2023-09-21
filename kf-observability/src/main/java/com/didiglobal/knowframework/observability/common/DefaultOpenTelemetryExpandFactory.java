package com.didiglobal.knowframework.observability.common;

import io.opentelemetry.sdk.trace.SpanProcessor;

import java.util.ArrayList;
import java.util.List;

public class DefaultOpenTelemetryExpandFactory implements OpenTelemetryExpandFactory {
    @Override
    public List<SpanProcessor> getSpanProcessorList() {
        return new ArrayList<>();
    }
}
