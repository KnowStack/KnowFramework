package com.didiglobal.logi.auvjob.common.domain;

import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.core.job.Job;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class JobInfo {
  private String jobCode;
  private String taskCode;
  private String className;
  private Integer retryTimes;
  private Integer tryTimes;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer status;
  private String error;
  private Long timeout;
  private Object result;
  private Job job;

  /**
   * auv job.
   *
   * @return auv job
   */
  public AuvJob getAuvJob() {
    AuvJob job = new AuvJob();
    job.setTaskCode(getTaskCode());
    job.setClassName(getClassName());
    job.setTryTimes(getTryTimes());
    job.setStartTime(LocalDateTime.now());
    job.setCreateTime(LocalDateTime.now());
    job.setUpdateTime(LocalDateTime.now());
    return job;
  }

  /**
   * auv job log.
   *
   * @return job log
   */
  public AuvJobLog getAuvJobLog() {
    AuvJobLog auvJobLog = new AuvJobLog();
    auvJobLog.setJobCode(getJobCode());
    auvJobLog.setTaskCode(getTaskCode());
    auvJobLog.setClassName(getClassName());
    auvJobLog.setTryTimes(getTryTimes());
    auvJobLog.setStartTime(getStartTime());
    auvJobLog.setEndTime(LocalDateTime.now());
    auvJobLog.setStatus(getStatus());
    auvJobLog.setError(getError() == null ? "" : getError());
    auvJobLog.setResult(getResult() == null ? "" : getResult().toString());
    auvJobLog.setCreateTime(LocalDateTime.now());
    auvJobLog.setUpdateTime(LocalDateTime.now());
    return auvJobLog;
  }

}