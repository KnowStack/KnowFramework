package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.common.domain.TaskInfo;
import com.didiglobal.logi.job.common.dto.JobDto;
import com.didiglobal.logi.job.common.dto.JobLogDto;
import java.util.List;
import java.util.concurrent.Future;

/**
 * job管理器，负责CRUD、启动、暂停job.
 *
 * @author dengshan
 */
public interface JobManager {

  /**
   * 启动任务.
   *
   * @param taskInfo 任务
   * @return future
   */
  Future<Object> start(TaskInfo taskInfo);

  Integer runningJobSize();

  /**
   * 停止任务.
   *
   * @param jobCode job code
   * @return true/false
   */
  boolean stop(String jobCode);

  int stopAll();

  List<JobDto> getJobs();

  List<JobLogDto> getJobLogs(String taskCode, Integer limit);

}
