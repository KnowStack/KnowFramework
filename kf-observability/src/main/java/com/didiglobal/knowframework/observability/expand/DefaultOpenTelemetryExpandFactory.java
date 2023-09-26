package com.didiglobal.knowframework.observability.expand;

import io.opentelemetry.sdk.trace.SpanProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author slhu
 */
public class DefaultOpenTelemetryExpandFactory implements OpenTelemetryExpandFactory {
    @Override
    public List<SpanProcessor> getSpanProcessorList() {
        return new ArrayList<>();
    }
}
