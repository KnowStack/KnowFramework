package com.didichuxing.datachannel.metrics.helper.convert.support;

import com.didichuxing.datachannel.metrics.Metric;
import com.didichuxing.datachannel.metrics.adapt.bean.MetricBean;
import com.didichuxing.datachannel.metrics.helper.convert.Converter;

/**
 * 
 * 
 * @author liujianhui
 * @version:2015年12月22日 下午7:16:17
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
