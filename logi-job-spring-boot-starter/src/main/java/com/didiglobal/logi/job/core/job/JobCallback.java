package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.common.domain.JobInfo;

public interface JobCallback {
  void callback(JobInfo jobInfo);
}
