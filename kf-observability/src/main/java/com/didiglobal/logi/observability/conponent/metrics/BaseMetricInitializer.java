package com.didiglobal.logi.observability.conponent.metrics;

import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.ObservabilityInitializer;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.Meter;
import org.apache.commons.collections4.MapUtils;
import java.util.Map;

public abstract class BaseMetricInitializer implements ObservabilityInitializer {

    private Meter meter;

    public BaseMetricInitializer() {
        this("metrics.custom");
    }

    public BaseMetricInitializer(String instrumentationScopeName) {
        this.meter = Observability.getMeter(instrumentationScopeName);
    }

    /**
     * @param metricName 指标名
     * @param metricDescription 指标描述
     * @param metricUnit 参考 MetricUnit 常量类
     * @param metricValue 指标值
     * @param tags 指标相关属性值
     */
    public void registerMetric(String metricName, String metricDescription, String metricUnit, Double metricValue, Map<String, String> tags) {
        AttributesBuilder attributesBuilder = Attributes.builder();
        if(MapUtils.isNotEmpty(tags)) {
            for(Map.Entry<String, String> entry : tags.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                attributesBuilder.put(key, value);
            }
        }
        meter
                .gaugeBuilder(metricName)
                .setDescription(metricDescription)
                .setUnit(metricUnit)
                .buildWithCallback(
                        result -> result.record(
                                metricValue,
                                attributesBuilder.build()
                        )
                );
    }

    /**
     * @param metricName 指标名
     * @param metricDescription 指标描述
     * @param metricUnit 参考 MetricUnit 常量类
     * @param metricValue 指标值
     */
    public void registerMetric(String metricName, String metricDescription, String metricUnit, Double metricValue) {
        meter
                .gaugeBuilder(metricName)
                .setDescription(metricDescription)
                .setUnit(metricUnit)
                .buildWithCallback(
                        result -> result.record(
                                metricValue
                        )
                );
    }

    /**
     * 指标注册函数，通过调用 registerMetric 函数进行自定义指标注册
     */
    public abstract void register();

    @Override
    public void startup() {
        register();
    }

}
