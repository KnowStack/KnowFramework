package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.enums.JobStatusEnum;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 * simple job factory.
 *
 * @author dengshan
 */
@Component
public class SimpleJobFactory implements JobFactory {

  private Map<String, Job> jobMap = new HashMap<>();

  @Override
  public void addJob(String className, Job job) {
    jobMap.put(className, job);
  }

  @Override
  public JobInfo newJob(TaskInfo taskInfo) {
    JobInfo jobInfo = new JobInfo();
    jobInfo.setTaskCode(taskInfo.getCode());
    jobInfo.setClassName(taskInfo.getClassName());
    jobInfo.setTryTimes(1);
    jobInfo.setStartTime(LocalDateTime.now());
    jobInfo.setStatus(JobStatusEnum.STARTED.getValue());
    jobInfo.setTimeout(taskInfo.getTimeout());
    jobInfo.setJob(jobMap.get(taskInfo.getClassName()));
    return jobInfo;
  }
}
