package com.didichuxing.datachannel.metrics.helper.convert.support;

import com.didichuxing.datachannel.metrics.MetricsTag;
import com.didichuxing.datachannel.metrics.adapt.bean.MetricTagBean;
import com.didichuxing.datachannel.metrics.helper.convert.Converter;

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
