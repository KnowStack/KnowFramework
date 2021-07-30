package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.common.domain.JobInfo;
import com.didiglobal.logi.job.common.domain.TaskInfo;

/**
 * job factory.
 *
 * @author dengshan
 */
public interface JobFactory {
  void addJob(String className, Job job);

  JobInfo newJob(TaskInfo taskInfo);
}
