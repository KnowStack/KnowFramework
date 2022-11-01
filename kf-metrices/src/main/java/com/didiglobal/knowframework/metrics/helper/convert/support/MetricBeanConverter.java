package com.didiglobal.knowframework.metrics.helper.convert.support;

import com.didiglobal.knowframework.metrics.adapt.bean.MetricBean;
import com.didiglobal.knowframework.metrics.helper.convert.Converter;
import com.didiglobal.knowframework.metrics.Metric;

/**
 * 
 * 
 * @author liujianhui
 */
public class MetricBeanConverter implements Converter<Metric, MetricBean> {

    @Override
    public MetricBean convert(Metric source) {
        MetricBean metricBean = new MetricBean();
        metricBean.setName(source.name());
        metricBean.setDescription(source.description());
        metricBean.setValue(source.value());
        return metricBean;
    }

}
