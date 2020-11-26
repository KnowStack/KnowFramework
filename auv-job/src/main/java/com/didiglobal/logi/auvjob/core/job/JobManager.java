package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.bean.JobInfo;
import com.didiglobal.logi.auvjob.common.bean.JobLog;
import com.didiglobal.logi.auvjob.query.JobLogQuery;

import java.util.List;
import java.util.concurrent.Future;

/**
 * job管理器，负责CRUD、启动、暂停job
 * @author dengshan
 */
public interface JobManager {

  /**
   * 启动任务
   * @param o todo 参数待定
   * @return
   */
  Future<Object> start(Job job);

  /**
   * 停止任务
   * @param o todo 参数待定
   * @return
   */
  boolean stop(Job job);

  boolean add(JobInfo jobInfo);

  boolean delete(String jobInfoCode);

  JobInfo getJob(String jobCode);

  List<JobInfo> getJobs();

  boolean addJobLog(JobLog jobLog);

  List<JobLog> getJobLogs();

  List<JobLog> getJobLogs(JobLogQuery jobLogQuery);

}
