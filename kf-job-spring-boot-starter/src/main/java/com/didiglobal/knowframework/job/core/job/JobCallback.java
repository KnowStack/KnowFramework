package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.domain.KfJob;

public interface JobCallback {
    void callback(KfJob kfJob);
}
