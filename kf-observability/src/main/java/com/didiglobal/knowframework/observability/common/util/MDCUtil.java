package com.didiglobal.knowframework.observability.common.util;


import io.opentelemetry.api.trace.Span;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * @author admin
 */
public class MDCUtil {

    /**
     * 日志跟踪标识
     */

    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    public static void putSpan(Span span) {
        if (Span.getInvalid() != span && span.getSpanContext().isValid()) {
            MDC.put(SPAN_ID, span.getSpanContext().getSpanId());
            MDC.put(TRACE_ID, span.getSpanContext().getTraceId());
        }
    }

    public static void removeSpanId(Span span) {
        if (StringUtils.equals(MDC.get(SPAN_ID), span.getSpanContext().getSpanId())) {
            MDC.remove(SPAN_ID);
        }
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID);
    }
}
