package com.didiglobal.knowframework.observability.exporter;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.bean.LogEvent;
import com.didiglobal.knowframework.observability.common.bean.Span;
import com.didiglobal.knowframework.observability.common.enums.LogEventType;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSpanExporter implements SpanExporter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingSpanExporter.class.getName());

    public static LoggingSpanExporter create() {
        return new LoggingSpanExporter();
    }

    public LoggingSpanExporter() {
        // pass
    }

    public CompletableResultCode export(Collection<SpanData> spans) {
        if (!isEnable()) {
            return CompletableResultCode.ofSuccess();
        }
        for (SpanData span : spans) {
            logger.info(
                    JSON.toJSONString(
                            new LogEvent(
                                    LogEventType.TRACE,
                                    new Span(span)
                            )
                    )
            );
        }
        return CompletableResultCode.ofSuccess();
    }

    public CompletableResultCode flush() {
        CompletableResultCode resultCode = new CompletableResultCode();
        return resultCode.succeed();
    }

    public CompletableResultCode shutdown() {
        return this.flush();
    }

    public boolean isEnable() {
        return Observability.exporterExist(this.getClass().getSimpleName());
    }
}
