package com.didiglobal.knowframework.job.examples;

import com.didiglobal.knowframework.observability.conponent.metrics.BaseMetricInitializer;
import com.didiglobal.knowframework.observability.conponent.metrics.Meter;
import com.didiglobal.knowframework.observability.conponent.metrics.Metric;
import com.didiglobal.knowframework.observability.conponent.metrics.MetricUnit;
import java.util.HashMap;
import java.util.Map;

public class MyMetricInitializer extends BaseMetricInitializer {

    @Override
    public void register() {
        super.registerMetric(
                "docs",
                "number of docs",
                MetricUnit.METRIC_UNIT_NUMBER,
                new Meter() {
                    @Override
                    public Metric getMetric() {
                        Map<String, String> tags = new HashMap<>();
                        tags.put("docType", "humanities");
                        return new Metric(1.0, tags);
                    }
                }
        );
    }

}
