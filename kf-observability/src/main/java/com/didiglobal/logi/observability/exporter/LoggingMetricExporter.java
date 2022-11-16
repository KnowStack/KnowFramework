package com.didiglobal.logi.observability.exporter;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.observability.common.bean.ExemplarData;
import com.didiglobal.logi.observability.common.bean.LogEvent;
import com.didiglobal.logi.observability.common.bean.Metric;
import com.didiglobal.logi.observability.common.enums.LogEventType;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.*;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoggingMetricExporter implements MetricExporter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingSpanExporter.class.getName());

    private final AggregationTemporality aggregationTemporality;

    public static LoggingMetricExporter create() {
        return create(AggregationTemporality.CUMULATIVE);
    }

    public static LoggingMetricExporter create(AggregationTemporality aggregationTemporality) {
        return new LoggingMetricExporter(aggregationTemporality);
    }

    /** @deprecated */
    @Deprecated
    public LoggingMetricExporter() {
        this(AggregationTemporality.CUMULATIVE);
    }

    private LoggingMetricExporter(AggregationTemporality aggregationTemporality) {
        this.aggregationTemporality = aggregationTemporality;
    }

    /** @deprecated */
    @Deprecated
    public AggregationTemporality getPreferredTemporality() {
        return this.aggregationTemporality;
    }

    public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
        return this.aggregationTemporality;
    }

    public CompletableResultCode export(Collection<MetricData> metrics) {
        for (MetricData metricData : metrics) {
            Collection<PointData> points = (Collection<PointData>) metricData.getData().getPoints();
            for (PointData pointData : points) {
                logger.info(
                        JSON.toJSONString(
                                new LogEvent(
                                        LogEventType.METRIC,
                                        buildMetricPoint(metricData, pointData)
                                )
                        )
                );
            }
        }
        return CompletableResultCode.ofSuccess();
    }

    private Metric buildMetricPoint(MetricData metricData, PointData pointData) {
        Metric metricPoint = new Metric();
        metricPoint.setMetricName(metricData.getName());
        metricPoint.setMetricType(metricData.getType().name());
        metricPoint.setMetricUnit(metricData.getUnit());
        metricPoint.setMetricDescription(metricData.getDescription());
        metricPoint.setInstrumentationScopeInfoName(metricData.getInstrumentationScopeInfo().getName());
        metricPoint.setMetricEpochNanos(pointData.getEpochNanos());
        metricPoint.setMetricValue(getMetricValue(pointData));
        metricPoint.setAttributes(pointData.getAttributes().asMap());
        List<ExemplarData> exemplarDataList = new ArrayList<>();
        for(io.opentelemetry.sdk.metrics.data.ExemplarData exemplarData : pointData.getExemplars()) {
            ExemplarData exemplarDataInstance = new ExemplarData();
            exemplarDataInstance.setTraceId(exemplarData.getSpanContext().getTraceId());
            exemplarDataInstance.setSpanId(exemplarData.getSpanContext().getSpanId());
            exemplarDataInstance.setEpochNanos(exemplarData.getEpochNanos());
        }
        metricPoint.setExemplarDataList(exemplarDataList);
        return metricPoint;
    }

    private Object getMetricValue(PointData pointData) {
        if(pointData instanceof DoublePointData) {
            return ((DoublePointData) pointData).getValue();
        } else if(pointData instanceof LongPointData) {
            return ((LongPointData) pointData).getValue();
        } else {
            // not support
            return 0d;
        }
    }

    public CompletableResultCode flush() {
        CompletableResultCode resultCode = new CompletableResultCode();
        return resultCode.succeed();
    }

    public CompletableResultCode shutdown() {
        return this.flush();
    }

}
