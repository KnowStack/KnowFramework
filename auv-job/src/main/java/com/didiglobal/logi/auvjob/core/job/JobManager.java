package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.query.JobLogQuery;
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

  /**
   * 停止任务.
   *
   * @param job job
   * @return true/false
   */
  boolean stop(Job job);

  boolean add(AuvJob auvJob);

  boolean delete(String jobInfoCode);

  AuvJob getJob(String jobCode);

  List<AuvJob> getJobs();

  boolean addJobLog(AuvJobLog auvJobLog);

  List<AuvJobLog> getJobLogs();

  List<AuvJobLog> getJobLogs(JobLogQuery jobLogQuery);

}
