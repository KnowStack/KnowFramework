package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.common.domain.LogIJob;
import com.didiglobal.logi.job.common.domain.LogITask;
import com.didiglobal.logi.job.common.enums.JobStatusEnum;
import com.didiglobal.logi.job.core.WorkerSingleton;
import com.didiglobal.logi.job.utils.IdWorker;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
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
  public LogIJob newJob(LogITask logITask) {
    LogIJob logIJob = new LogIJob();
    logIJob.setCode(IdWorker.getIdStr());
    logIJob.setTaskCode(logITask.getCode());
    logIJob.setClassName(logITask.getClassName());
    logIJob.setWorkerCode(WorkerSingleton.getInstance().getLogIWorker().getCode());
    logIJob.setWorkerIp(WorkerSingleton.getInstance().getLogIWorker().getIp());
    logIJob.setTryTimes(logITask.getRetryTimes() == null ? 1 : logITask.getRetryTimes());
    logIJob.setStatus(JobStatusEnum.STARTED.getValue());
    logIJob.setTimeout( logITask.getTimeout());
    logIJob.setJob(jobMap.get(logITask.getClassName()));
    logIJob.setTaskCallback(logITask.getTaskCallback());
    logIJob.setAppName(logITask.getAppName());
    return logIJob;
  }
}
