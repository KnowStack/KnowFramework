package com.didiglobal.knowframework.observability.conponent.metrics;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.ObservabilityInitializer;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import org.apache.commons.collections4.MapUtils;
import java.util.Map;
import java.util.function.Consumer;

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
     * @param meter 指标获取接口实例
     */
    public void registerMetric(String metricName, String metricDescription, String metricUnit, com.didiglobal.knowframework.observability.conponent.metrics.Meter meter) {
        this.meter
                .gaugeBuilder(metricName)
                .setDescription(metricDescription)
                .setUnit(metricUnit)
                .buildWithCallback(
                        new Consumer<ObservableDoubleMeasurement>() {
                            @Override
                            public void accept(ObservableDoubleMeasurement observableDoubleMeasurement) {
                                Metric metric = meter.getMetric();
                                Double metricValue = metric.getValue();
                                Map<String, String> tags = metric.getTags();
                                if(MapUtils.isNotEmpty(tags)) {
                                    AttributesBuilder attributesBuilder = Attributes.builder();
                                    for(Map.Entry<String, String> entry : tags.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        attributesBuilder.put(key, value);
                                    }
                                    observableDoubleMeasurement.record(metricValue, attributesBuilder.build());
                                } else {
                                    observableDoubleMeasurement.record(metricValue);
                                }
                            }
                        }
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
