package com.didiglobal.logi.job.common;

import com.didiglobal.logi.observability.Observability;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskResult implements Serializable {

    public static final long serialVersionUID = 42L;

    private int code;
    private String message;
    private String traceId;
    private String spanId;

    public static final int SUCCESS_CODE = 1;
    public static final int RUNNING_CODE = 0;
    public static final int FAIL_CODE = -1;

    public static final TaskResult SUCCESS = new TaskResult(SUCCESS_CODE, "scuucessed");
    public static final TaskResult FAIL = new TaskResult(FAIL_CODE, "failed");

    public TaskResult() {
    }

    public TaskResult(int code, String message) {
        this(code, message, Observability.getCurrentTraceId(), Observability.getCurrentSpanId());
    }

    public TaskResult(int code, String message, String traceId, String spanId) {
        this.code = code;
        this.message = message;
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public static TaskResult buildSuccess() {
        return new TaskResult(SUCCESS_CODE, "scuucessed");
    }

    public static TaskResult buildFail() {
        return new TaskResult(FAIL_CODE, "failed");
    }

}
