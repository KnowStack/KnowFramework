package com.didiglobal.knowframework.metrics.sink;

import com.didiglobal.knowframework.metrics.sink.mq.AbstractMetricSink;
import org.apache.log4j.Logger;

/**
 * 指标输出到文件系统中
 * 
 * @author liujianhui
 */
public class LoggerMetricSink extends AbstractMetricSink {

    private static final Logger METRIC_LOGGER = Logger.getLogger("metric");

    @Override
    public void sendMetrics(String content) {
        METRIC_LOGGER.info(content);
    }

}
