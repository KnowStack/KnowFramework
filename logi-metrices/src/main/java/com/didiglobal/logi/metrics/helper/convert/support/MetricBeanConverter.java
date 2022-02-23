package com.didiglobal.logi.metrics.helper.convert.support;

import com.didiglobal.logi.metrics.Metric;
import com.didiglobal.logi.metrics.adapt.bean.MetricBean;
import com.didiglobal.logi.metrics.helper.convert.Converter;

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
