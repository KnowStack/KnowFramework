package com.didiglobal.knowframework.log.log4j2.appender;

import com.didiglobal.knowframework.observability.conponent.metrics.BaseMetricInitializer;
import com.didiglobal.knowframework.observability.conponent.metrics.Meter;
import com.didiglobal.knowframework.observability.conponent.metrics.Metric;
import com.didiglobal.knowframework.observability.conponent.metrics.MetricUnit;

public class ElasticsearchAppenderMetricsInitializer extends BaseMetricInitializer {

    /**
     * 日志丢弃数
     */
    private static volatile Integer logEventDiscardNumber = 0;
    /**
     * 写入 elasticsearch 失败日志条数
     */
    private static volatile Integer logEventInsertFailedNumber = 0;

    @Override
    public void register() {
        super.registerMetric(
                "elasticsearch.appender.log.discard.number",
                "因 buffer 满导致被丢弃的日志条数。单位：个，类型：Double",
                MetricUnit.METRIC_UNIT_NUMBER,
                new Meter() {
                    @Override
                    public Metric getMetric() {
                        return new Metric(getLogEventDiscardNumber().doubleValue());
                    }
                }
        );
        super.registerMetric(
                "elasticsearch.appender.log.insert.failed.number",
                "写入 elasticsearch 失败的日志条数。单位：个，类型：Double",
                MetricUnit.METRIC_UNIT_NUMBER,
                new Meter() {
                    @Override
                    public Metric getMetric() {
                        return new Metric(getLogEventInsertFailedNumber().doubleValue());
                    }
                }
        );
    }

    private static synchronized Integer getLogEventDiscardNumber() {
        Integer result = logEventDiscardNumber;
        logEventDiscardNumber = 0;
        return result;
    }

    private static synchronized Integer getLogEventInsertFailedNumber() {
        Integer result = logEventInsertFailedNumber;
        logEventInsertFailedNumber = 0;
        return result;
    }

    public static synchronized void logEventDiscardNumberIncr() {
        logEventDiscardNumber++;
    }

    public static synchronized void logEventInsertFailedNumberIncr(Integer number) {
        logEventInsertFailedNumber += number;
    }

}
