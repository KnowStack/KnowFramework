package com.didiglobal.knowframework.observability.conponent.metrics;

public interface Meter {

    /**
     * @return 返回指标值
     */
    Metric getMetric();

}
