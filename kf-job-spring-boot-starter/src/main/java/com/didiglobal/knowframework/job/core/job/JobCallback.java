package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.domain.LogIJob;

public interface JobCallback {
    void callback(LogIJob logIJob);
}
