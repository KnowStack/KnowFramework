package com.didiglobal.logi.job.extend;

import java.io.IOException;
import java.util.List;

public interface JobLogFetcherExtend {

    /**
     * @param traceId
     * @return 返回 trace id 相关上下文日志集
     */
    List<String> getLogsByTraceIdFromExternalSystem(String traceId) throws Exception;

}
