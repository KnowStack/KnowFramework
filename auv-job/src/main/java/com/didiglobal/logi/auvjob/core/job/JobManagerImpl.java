package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.bean.JobInfo;
import com.didiglobal.logi.auvjob.common.bean.JobLog;
import com.didiglobal.logi.auvjob.mapper.JobInfoMapper;
import com.didiglobal.logi.auvjob.mapper.JobLogMapper;
import com.didiglobal.logi.auvjob.query.JobLogQuery;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author dengshan
 */
public class JobManagerImpl implements JobManager {

  private JobThreadPoolExecutor threadPoolExecutor;
  private SqlSession session;

  private JobInfoMapper jobInfoMapper;
  private JobLogMapper jobLogMapper;
  private List<Future<Object>> futures = new ArrayList<>(100);

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
  public Future<Object> start(Job job) {
    final Future jobFuture = threadPoolExecutor.submmit(new JobHandler(job));
    futures.add(jobFuture);
    // todo task execute / job start?
    // todo 成功，失败怎么打日志；超时怎么退出并打日志
    return jobFuture;
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
   */
  class JobHandler implements Callable {

    private Job job;

    public JobHandler(Job job) {
      this.job = job;
    }

    @Override
    public Object call() throws Exception {
      return job.execute(null);
    }
  }


}
