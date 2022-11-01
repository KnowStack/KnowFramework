package com.didiglobal.knowframework.metrics.helper.convert.support;

import com.didiglobal.knowframework.metrics.adapt.bean.MetricTagBean;
import com.didiglobal.knowframework.metrics.helper.convert.Converter;
import com.didiglobal.knowframework.metrics.MetricsTag;

public class MetricTagBeanConverter implements Converter<MetricsTag, MetricTagBean> {

    @Override
    public MetricTagBean convert(MetricsTag source) {
        MetricTagBean metricTagBean = new MetricTagBean();
        metricTagBean.setDescription(source.description());
        metricTagBean.setName(source.name());
        metricTagBean.setValue(source.value());
        return metricTagBean;
    }

}
