package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;

/**
 * job factory.
 *
 * @author dengshan
 */
public interface JobFactory {
  JobInfo newJob(TaskInfo taskInfo);
}
