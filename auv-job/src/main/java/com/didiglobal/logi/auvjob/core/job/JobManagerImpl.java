package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.Tuple;
import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.enums.JobStatusEnum;
import com.didiglobal.logi.auvjob.mapper.AuvJobLogMapper;
import com.didiglobal.logi.auvjob.mapper.AuvJobMapper;
import com.didiglobal.logi.auvjob.query.JobLogQuery;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job manager impl.
 *
 * @author dengshan
 */
public class JobManagerImpl implements JobManager {
  private static final Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);

  private JobThreadPoolExecutor threadPoolExecutor;
  private SqlSession session;
  private JobFactory jobFactory;

  private AuvJobMapper auvJobMapper;
  private AuvJobLogMapper jobLogMapper;
  private List<Tuple<JobInfo, Future>> jobFutures = new ArrayList<>(100);

  /**
   * constructor.
   *
   * @param threadPoolExecutor executor
   * @param session sqlSession
   * @param jobFactory job factory
   */
  public JobManagerImpl(JobThreadPoolExecutor threadPoolExecutor, SqlSession session,
                        JobFactory jobFactory) {
    this.threadPoolExecutor = threadPoolExecutor;
    this.session = session;
    this.jobFactory = jobFactory;
    this.auvJobMapper = session.getMapper(AuvJobMapper.class);
    this.jobLogMapper = session.getMapper(AuvJobLogMapper.class);
    initialize();
  }

  private void initialize() {
    new Thread(new JobFutureHandler(jobFutures), "JobFutureHandler Thread").start();
  }

  @Override
  public Future<Object> start(TaskInfo taskInfo) {
    JobInfo jobInfo = jobFactory.newJob(taskInfo);
    AuvJob job = jobInfo.getAuvJob();
    auvJobMapper.insert(job);
    jobInfo.setJobCode(job.getCode());

    final Future jobFuture = threadPoolExecutor.submmit(new JobHandler(jobInfo));
    jobFutures.add(new Tuple<>(jobInfo, jobFuture));
    return jobFuture;
  }

  @Override
  public boolean stop(Job job) {
    return false;
  }

  @Override
  public boolean add(AuvJob auvJob) {
    return false;
  }

  @Override
  public boolean delete(String jobInfoCode) {
    return false;
  }

  @Override
  public AuvJob getJob(String jobCode) {
    return null;
  }

  @Override
  public List<AuvJob> getJobs() {
    return null;
  }

  @Override
  public boolean addJobLog(AuvJobLog auvJobLog) {
    return false;
  }

  @Override
  public List<AuvJobLog> getJobLogs() {
    return null;
  }

  @Override
  public List<AuvJobLog> getJobLogs(JobLogQuery jobLogQuery) {
    return null;
  }

  /**
   * job 执行线程.
   */
  class JobHandler implements Callable {

    private JobInfo jobInfo;

    public JobHandler(JobInfo jobInfo) {
      this.jobInfo = jobInfo;
    }

    @Override
    public Object call() throws Exception {
      Object object = null;
      try {
        jobInfo.setStartTime(LocalDateTime.now());
        object = jobInfo.getJob().execute(null);
        jobInfo.setStatus(JobStatusEnum.SUCCEED.getValue());
        jobInfo.setError("");
      } catch (Exception e) {
        jobInfo.setStatus(JobStatusEnum.FAILED.getValue());
        // 记录任务异常信息
        String errorMessage = String.format("StackTrace[%s] || Message[%s]",
                e.getStackTrace(), e.getMessage());
        jobInfo.setError(errorMessage);
        logger.error("class=JobHandler||method=call||url=||msg={}", e);
      }
      jobInfo.setEndTime(LocalDateTime.now());
      jobInfo.setError(jobInfo.getError() == null ? "" : jobInfo.getError());
      jobInfo.setResult(object);
      return object;
    }
  }

  /**
   * Job 执行清理县城，对超时的要主动杀死，执行完的收集信息并记录日志.
   */
  class JobFutureHandler implements Runnable {
    private static final long JOB_FUTURE_CLEAN_INTERVAL = 30L;
    private List<Tuple<JobInfo, Future>> jobFutures;

    public JobFutureHandler(List<Tuple<JobInfo, Future>> jobFutures) {
      this.jobFutures = jobFutures;
    }

    @Override
    public void run() {
      while (true) {
        // 处理已完成任务
        Iterator<Tuple<JobInfo, Future>> iterator = jobFutures.iterator();
        while (iterator.hasNext()) {
          Tuple<JobInfo, Future> jobFuture = iterator.next();
          Future future = jobFuture.getV2();
          if (future.isDone()) {
            // 删除auvJob
            JobInfo jobInfo = jobFuture.getV1();
            auvJobMapper.deleteById(jobInfo.getJobCode());

            // 增加auvJobLog
            AuvJobLog auvJobLog = jobInfo.getAuvJobLog();
            jobLogMapper.insert(auvJobLog);

            // 移除记录
            iterator.remove();
          }
        }

        // 处理超时任务
        for (Tuple<JobInfo, Future> jobFuture : jobFutures) {
          JobInfo jobInfo = jobFuture.getV1();

          LocalDateTime startTime = jobInfo.getStartTime();
          LocalDateTime now = LocalDateTime.now();
          Duration between = Duration.between(now, startTime);
          Long timeout = jobInfo.getTimeout();

          Future future = jobFuture.getV2();
          if (between.get(ChronoUnit.MICROS) > timeout && !future.isDone()) {
            future.cancel(true);
            jobInfo.setStatus(JobStatusEnum.CANCELED.getValue());
          }
        }

        // 间隔一段时间执行一次
        ThreadUtil.sleep(JOB_FUTURE_CLEAN_INTERVAL, TimeUnit.SECONDS);
      }
    }
  }

}
