package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.enums.JobStatusEnum;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * simple job factory.
 *
 * @author dengshan
 */
public class SimpleJobFactory implements JobFactory {

  @Override
  public JobInfo newJob(TaskInfo taskInfo) {
    JobInfo jobInfo = new JobInfo();
    jobInfo.setTaskCode(taskInfo.getCode());
    jobInfo.setClassName(taskInfo.getClassName());
    jobInfo.setTryTimes(1);
    jobInfo.setStartTime(LocalDateTime.now());
    jobInfo.setStatus(JobStatusEnum.STARTED.getValue());
    jobInfo.setTimeout(taskInfo.getTimeout());
    jobInfo.setJob(new Job() {
      @Override
      public Object execute(JobContext jobContext) {
        ThreadUtil.sleep(10L, TimeUnit.SECONDS);
        return "hello world";
      }
    });
    return jobInfo;
  }
}
