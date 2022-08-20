package com.didiglobal.logi.log.util;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.observability.common.bean.Log;
import com.didiglobal.logi.observability.common.bean.LogEvent;
import com.didiglobal.logi.observability.common.enums.LogEventType;
import io.opentelemetry.api.trace.Span;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jinbinbin
 * @version $Id: FlagWrapper.java, v 0.1 2017年12月19日 22:52 jinbinbin Exp $
 */
public class FlagWrapper {

    public static String wrapMessage(String message) {
        if (null == message) {
            return "";
        }
        Span span = Span.current();
        if(Span.getInvalid() != span && span.getSpanContext().isValid()) {
            //当前上下文不存在于任何span中
            return JSON.toJSONString(
                    new LogEvent(
                            LogEventType.LOG,
                            new Log(StringUtils.EMPTY, StringUtils.EMPTY, message)
                    )
            );
        } else {
            String tracerId = span.getSpanContext().getTraceId();
            String spanId = span.getSpanContext().getSpanId();
            return JSON.toJSONString(
                            new LogEvent(
                                    LogEventType.LOG,
                                    new Log(tracerId, spanId, message)
                            )
                    );
        }
    }

    public static String wrapExceptionMessage(String message) {
        return wrapMessage(message);
    }

}
