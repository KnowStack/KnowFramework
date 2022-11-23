package com.didiglobal.knowframework.observability.conponent.metrics;

import lombok.Data;

import java.util.Map;

@Data
public class Metric {

    private Double value;
    private Map<String, String> tags;

    public Metric(Double value, Map<String, String> tags) {
        this.value = value;
        this.tags = tags;
    }

    public Metric(Double value) {
        this.value = value;
    }

    public Metric() {
    }

}
