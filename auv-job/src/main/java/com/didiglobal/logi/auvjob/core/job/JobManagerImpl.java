package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.bean.JobInfo;
import com.didiglobal.logi.auvjob.bean.JobLog;
import com.didiglobal.logi.auvjob.mapper.JobInfoMapper;
import com.didiglobal.logi.auvjob.mapper.JobLogMapper;
import com.didiglobal.logi.auvjob.query.JobLogQuery;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author dengshan
 */
public class JobManagerImpl implements JobManager {

  private JobThreadPoolExecutor threadPoolExecutor;
  private SqlSession session;

  private JobInfoMapper jobInfoMapper;
  private JobLogMapper jobLogMapper;

  public JobManagerImpl(JobThreadPoolExecutor threadPoolExecutor, SqlSession session) {
    this.threadPoolExecutor = threadPoolExecutor;
    this.session = session;
    initialize();
  }

  private void initialize() {
    this.jobInfoMapper = session.getMapper(JobInfoMapper.class);
    this.jobLogMapper = session.getMapper(JobLogMapper.class);
  }

  @Override
  public boolean start(Job job) {
    threadPoolExecutor.submmit(new JobThread(job));
    return false;
  }

  @Override
  public boolean stop(Job job) {
    return false;
  }

  @Override
  public boolean add(JobInfo jobInfo) {
    return false;
  }

  @Override
  public boolean delete(String jobInfoCode) {
    return false;
  }

  @Override
  public JobInfo getJob(String jobCode) {
    return null;
  }

  @Override
  public List<JobInfo> getJobs() {
    return null;
  }

  @Override
  public boolean addJobLog(JobLog jobLog) {
    return false;
  }

  @Override
  public List<JobLog> getJobLogs() {
    return null;
  }

  @Override
  public List<JobLog> getJobLogs(JobLogQuery jobLogQuery) {
    return null;
  }

  /**
   * job 执行线程
   * todo 是否有返回值待定
   */
  class JobThread implements Runnable {

    private Job job;

    public JobThread(Job job) {
      this.job = job;
    }

    @Override
    public void run() {
      // job.execute();
    }
  }


}
