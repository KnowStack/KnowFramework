package com.didiglobal.logi.auvjob.common.domain;

import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.core.job.Job;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class JobInfo {
  private String code;
  private String taskCode;
  private String className;
  private Integer retryTimes;
  private Integer tryTimes;
  private Timestamp startTime;
  private Timestamp endTime;
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
    job.setCode(getCode());
    job.setTaskCode(getTaskCode());
    job.setClassName(getClassName());
    job.setTryTimes(getTryTimes());
    return job;
  }

  /**
   * auv job log.
   *
   * @return job log
   */
  public AuvJobLog getAuvJobLog() {
    AuvJobLog auvJobLog = new AuvJobLog();
    auvJobLog.setJobCode(getCode());
    auvJobLog.setTaskCode(getTaskCode());
    auvJobLog.setClassName(getClassName());
    auvJobLog.setTryTimes(getTryTimes());
    auvJobLog.setStartTime(getStartTime());
    auvJobLog.setEndTime(new Timestamp(System.currentTimeMillis()));
    auvJobLog.setStatus(getStatus());
    auvJobLog.setError(getError() == null ? "" : getError());
    auvJobLog.setResult(getResult() == null ? "" : getResult().toString());
    return auvJobLog;
  }

}