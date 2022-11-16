package com.didiglobal.logi.job.examples;

import com.didiglobal.logi.observability.conponent.metrics.BaseMetricInitializer;
import com.didiglobal.logi.observability.conponent.metrics.MetricUnit;
import java.util.HashMap;
import java.util.Map;

public class MyMetricinitializer extends BaseMetricInitializer {

    @Override
    public void register() {
        Map<String, String> tags = new HashMap<>();
        tags.put("docType", "humanities");
        super.registerMetric(
                "docs",
                "number of docs",
                MetricUnit.METRIC_UNIT_NUMBER,
                1.0,
                tags
        );
    }

}
