package com.didiglobal.logi.observability.metrics;

import com.didiglobal.logi.observability.Observability;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;

public class MetricsTest {
    public static void main(String[] args) {

        Meter sampleMeter = Observability.getMeter("io.opentelemetry.example.metrics");

        sampleMeter
                .gaugeBuilder("jvm.memory.total")
                .setDescription("Reports JVM memory usage.")
                .setUnit("byte")
                .buildWithCallback(
                        result -> result.record(1.0d, Attributes.empty()));

        sampleMeter
                .gaugeBuilder("jvm.memory.usage")
                .setDescription("Reports JVM memory usage.")
                .setUnit("byte")
                .buildWithCallback(
                        result -> result.record(0.0d, Attributes.empty()));

//        DoubleHistogram doubleHistogram = sampleMeter.histogramBuilder("http.response.time")
//                .setDescription("Reports http response time")
//                .setUnit("second")
//                .build();
//        for (int i = 0; i < 10; i++) {
//            doubleHistogram.record(i);
//        }

    }
}

