package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.bean.TaskInfo;

/**
 * @author dengshan
 */
public interface JobFactory {
  Job newJob(TaskInfo taskInfo);
}
