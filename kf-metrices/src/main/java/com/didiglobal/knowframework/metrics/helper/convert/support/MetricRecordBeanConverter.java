package com.didiglobal.knowframework.metrics.helper.convert.support;

import java.util.ArrayList;
import java.util.List;

import com.didiglobal.knowframework.metrics.adapt.bean.MetricBean;
import com.didiglobal.knowframework.metrics.adapt.bean.MetricRecordBean;
import com.didiglobal.knowframework.metrics.adapt.bean.MetricTagBean;
import com.didiglobal.knowframework.metrics.helper.convert.Converter;
import com.didiglobal.knowframework.metrics.Metric;
import com.didiglobal.knowframework.metrics.MetricsRecord;
import com.didiglobal.knowframework.metrics.MetricsTag;
import com.didiglobal.knowframework.metrics.helper.convert.ConversionService;

/**
 * 
 * 
 * @author liujianhui
 */
public class MetricRecordBeanConverter implements Converter<MetricsRecord, MetricRecordBean> {
    private ConversionService conversionService;

    public MetricRecordBeanConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public MetricRecordBean convert(MetricsRecord source) {
        MetricRecordBean metricRecordBean = new MetricRecordBean();
        metricRecordBean.setTimestamp(source.timestamp());
        metricRecordBean.setName(source.name());

        //convert tag
        List<MetricTagBean> tagBeans = new ArrayList<MetricTagBean>();
        Iterable<MetricsTag> tags = source.tags();
        if (null != tags) {
            for (MetricsTag loopTag : tags) {
                tagBeans.add(conversionService.convert(loopTag, MetricTagBean.class));
            }
        }
        metricRecordBean.setMetricTags(tagBeans);

        //convert metric
        List<MetricBean> metricBeans = new ArrayList<MetricBean>();
        Iterable<Metric> metrics = source.metrics();
        if (null != metrics) {
            for (Metric loopMetric : metrics) {
                metricBeans.add(conversionService.convert(loopMetric, MetricBean.class));
            }
        }
        metricRecordBean.setMetrics(metricBeans);

        return metricRecordBean;
    }

}
