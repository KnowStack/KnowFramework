package com.didiglobal.logi.observability.common.bean;

import com.didiglobal.logi.observability.common.NetworkUtils;
import com.didiglobal.logi.observability.common.enums.LogEventType;
import lombok.Data;

@Data
public class LogEvent {

    private LogEventType logEventType;
    private Object data;
    private String hostName;
    private String ip;

    public LogEvent(LogEventType logEventType, Object data) {
        this.logEventType = logEventType;
        this.data = data;
        this.ip = NetworkUtils.getHostIp();
        this.hostName = NetworkUtils.getHostName();
    }

    public LogEvent() {

    }

}
