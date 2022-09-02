package com.didiglobal.logi.job.extend.impl;

import com.didiglobal.logi.job.extend.JobLogFetcherExtend;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("defaultJobLogFetcherExtendImpl")
public class DefaultJobLogFetcherExtendImpl implements JobLogFetcherExtend {

    @Override
    public List<String> getLogsByTraceIdFromExternalSystem(String traceId) {
        return new ArrayList<>();
    }

}
