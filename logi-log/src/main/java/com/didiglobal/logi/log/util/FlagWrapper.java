package com.didiglobal.logi.log.util;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.common.bean.Log;
import com.didiglobal.logi.observability.common.bean.LogEvent;
import com.didiglobal.logi.observability.common.enums.LogEventType;

/**
 * @author jinbinbin
 * @version $Id: FlagWrapper.java, v 0.1 2017年12月19日 22:52 jinbinbin Exp $
 */
public class FlagWrapper {

    public static String wrapMessage(String message) {
        if (null == message) {
            return "";
        }
        return JSON.toJSONString(
                            new LogEvent(
                                    LogEventType.LOG,
                                    new Log(Observability.getCurrentTraceId(), Observability.getCurrentSpanId(), message)
                            )
                    );

    }

    public static String wrapExceptionMessage(String message) {
        return wrapMessage(message);
    }

}
