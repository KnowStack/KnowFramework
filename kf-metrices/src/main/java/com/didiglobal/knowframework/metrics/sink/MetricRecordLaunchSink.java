package com.didiglobal.knowframework.metrics.sink;

import com.didiglobal.knowframework.metrics.MetricsRecord;
import com.didiglobal.knowframework.metrics.MetricsSink;
import com.didiglobal.knowframework.metrics.MetricsSystem;
import com.didiglobal.knowframework.metrics.adapt.bean.MetricRecordBean;
import com.didiglobal.knowframework.metrics.helper.convert.SimpleConversionService;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetricRecordLaunchSink implements MetricsSink {
    private static final Log LOG = LogFactory.getLog(MetricRecordLaunchSink.class);

    private Launcher         launcher;

    private MetricsSystem metricsSystem;

    @Override
    public void init(SubsetConfiguration conf) {
    }

    @Override
    public void putMetrics(MetricsRecord record) {
        MetricRecordBean metricRecordBean = SimpleConversionService.getInstance().convert(record,
            MetricRecordBean.class);
        launcher.launch(metricRecordBean);

        if (LOG.isDebugEnabled()) {
            LOG.debug("success to send record " + metricRecordBean);
        }
    }

    public void init() {
        metricsSystem.register("MetricRecordLaunchSink", "", this);
        LOG.info("success register MetricRecordLaunchSink ");
    }

    @Override
    public void flush() {
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public MetricsSystem getMetricsSystem() {
        return metricsSystem;
    }

    public void setMetricsSystem(MetricsSystem metricsSystem) {
        this.metricsSystem = metricsSystem;
    }

    public interface Launcher {
        void launch(MetricRecordBean metricRecord);
    }
}
