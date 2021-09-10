package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.common.domain.LogIJob;
import com.didiglobal.logi.job.common.domain.LogITask;

/**
 * job factory.
 *
 * @author dengshan
 */
public interface JobFactory {
  void addJob(String className, Job job);

  LogIJob newJob(LogITask logITask);
}
