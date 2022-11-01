package com.didiglobal.knowframework.metrics.lib;

import com.didiglobal.knowframework.metrics.Metric;
import com.didiglobal.knowframework.metrics.MetricsVisitor;

/**
 * 
 * 
 * @author liujianhui
 */
public class MetricReference<T> extends Metric {

    private final T value;

    public MetricReference(String name, String description, T value) {
        super(name, description);
        this.value = value;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public void visit(MetricsVisitor visitor) {
        //TODO
    }

}
