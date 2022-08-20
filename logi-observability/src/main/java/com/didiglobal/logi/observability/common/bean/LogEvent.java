package com.didiglobal.logi.observability.common.bean;

import com.didiglobal.logi.observability.common.enums.LogEventType;
import lombok.Data;

@Data
public class LogEvent {

    private LogEventType logEventType;
    private Object data;

    public LogEvent(LogEventType logEventType, Object data) {
        this.logEventType = logEventType;
        this.data = data;
    }

    public LogEvent() {
    }
}
