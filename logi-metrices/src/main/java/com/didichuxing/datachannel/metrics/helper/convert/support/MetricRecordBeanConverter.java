package com.didichuxing.datachannel.metrics.helper.convert.support;

import java.util.ArrayList;
import java.util.List;

import com.didichuxing.datachannel.metrics.Metric;
import com.didichuxing.datachannel.metrics.MetricsRecord;
import com.didichuxing.datachannel.metrics.MetricsTag;
import com.didichuxing.datachannel.metrics.adapt.bean.MetricBean;
import com.didichuxing.datachannel.metrics.adapt.bean.MetricRecordBean;
import com.didichuxing.datachannel.metrics.adapt.bean.MetricTagBean;
import com.didichuxing.datachannel.metrics.helper.convert.ConversionService;
import com.didichuxing.datachannel.metrics.helper.convert.Converter;

/**
 * 
 * 
 * @author liujianhui
 * @version:2015年12月22日 下午7:16:22
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
