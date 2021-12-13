package com.didiglobal.logi.metrics.lib;

import com.didiglobal.logi.metrics.MetricsRecordBuilder;

/**
 * 周期性的性能指标，每次快照后都会重新设置
 * 
 * @author liujianhui
 */
public abstract class MetricMutablePeriodGauge<T extends Number> extends MetricMutable {

    public MetricMutablePeriodGauge(String name, String description) {
        super(name, description);
    }

    /**
     * Increment the value of the metric by 1
     */
    public abstract void incr();

    /**
     * Decrement the value of the metric by 1
     */
    public abstract void decr();

    public abstract void reset();

    public abstract void doSnapshot(MetricsRecordBuilder builder, boolean all);

    @Override
    public void snapshot(MetricsRecordBuilder builder, boolean all) {
        if (all || changed()) {
            doSnapshot(builder, all);
            clearChanged();
            reset();
        }
    }
}
