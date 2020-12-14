package com.didiglobal.logi.auvjob.core.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.auvjob.common.Tuple;
import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.dto.JobDto;
import com.didiglobal.logi.auvjob.common.dto.JobLogDto;
import com.didiglobal.logi.auvjob.common.enums.JobStatusEnum;
import com.didiglobal.logi.auvjob.core.task.TaskCallback;
import com.didiglobal.logi.auvjob.mapper.AuvJobLogMapper;
import com.didiglobal.logi.auvjob.mapper.AuvJobMapper;
import com.didiglobal.logi.auvjob.utils.Assert;
import com.didiglobal.logi.auvjob.utils.BeanUtil;
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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * job manager impl.
 *
 * @author dengshan
 */
@Service
public class JobManagerImpl implements JobManager {
  private static final Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);

  private JobFactory jobFactory;
  private AuvJobMapper auvJobMapper;
  private AuvJobLogMapper auvJobLogMapper;
  private JobExecutor jobExecutor;

  private List<Tuple<JobInfo, Future>> jobFutures = new ArrayList<>(100);

  /**
   * constructor.
   *
   */
  @Autowired
  public JobManagerImpl(JobFactory jobFactory, AuvJobMapper auvJobMapper,
                        AuvJobLogMapper auvJobLogMapper, JobExecutor jobExecutor) {
    this.jobFactory = jobFactory;
    this.auvJobMapper = auvJobMapper;
    this.auvJobLogMapper = auvJobLogMapper;
    this.jobExecutor = jobExecutor;
    initialize();
  }

  private void initialize() {
    new Thread(new JobFutureHandler(jobFutures), "JobFutureHandler Thread").start();
  }

  @Override
  public Future<Object> start(TaskInfo taskInfo) {
    // 添加job信息
    JobInfo jobInfo = jobFactory.newJob(taskInfo);
    AuvJob job = jobInfo.getAuvJob();
    auvJobMapper.insert(job);
    jobInfo.setJobCode(job.getCode());

    Future jobFuture = jobExecutor.submit(new JobHandler(jobInfo,
            taskInfo.getTaskCallback()));
    jobFutures.add(new Tuple<>(jobInfo, jobFuture));
    return jobFuture;
  }

  @Override
  public Integer runningJobSize() {
    return this.jobFutures.size();
  }

  @Override
  public boolean stop(String jobCode) {
    Assert.notNull(jobCode, "jobCode can not be null!");
    for (Tuple<JobInfo, Future> jobFuture : jobFutures) {
      if (jobCode.equals(jobFuture.getV1().getJobCode())) {
        Future future = jobFuture.getV2();
        if (!future.isDone()) {
          return future.cancel(true);
        }
      }
    }
    return false;
  }

  @Override
  public int stopAll() {
    int succeedNum = 0;
    for (Tuple<JobInfo, Future> jobFuture : jobFutures) {
      Future future = jobFuture.getV2();
      if (!future.isDone() && future.cancel(true)) {
        succeedNum++;
      }
    }
    return succeedNum;
  }

  @Override
  public List<JobDto> getJobs() {
    List<AuvJob> auvJobs = auvJobMapper.selectList(new QueryWrapper<>());
    if (CollectionUtils.isEmpty(auvJobs)) {
      return null;
    }
    List<JobDto> jobDtos = auvJobs.stream().map(auvJob -> BeanUtil.convertTo(auvJob, JobDto.class))
            .collect(Collectors.toList());
    return jobDtos;
  }

  @Override
  public List<JobLogDto> getJobLogs(String taskCode, Integer limit) {
    List<AuvJobLog> auvJobLogs = auvJobLogMapper.selectList(new QueryWrapper<AuvJobLog>()
            .eq("taskCode", taskCode).last("limit " + limit));
    if (CollectionUtils.isEmpty(auvJobLogs)) {
      return null;
    }
    List<JobLogDto> jobLogDtos = auvJobLogs.stream().map(auvJobLog ->
            BeanUtil.convertTo(auvJobLog, JobLogDto.class)).collect(Collectors.toList());
    return jobLogDtos;
  }

  /**
   * job 执行线程.
   */
  class JobHandler implements Callable {

    private JobInfo jobInfo;
    private TaskCallback taskCallback;

    public JobHandler(JobInfo jobInfo, TaskCallback taskCallback) {
      this.jobInfo = jobInfo;
      this.taskCallback = taskCallback;
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
      } finally {
        // job callback, 释放任务锁
        taskCallback.callback(jobInfo.getTaskCode());
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
    private static final long JOB_FUTURE_CLEAN_INTERVAL = 10L;
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
            auvJobLogMapper.insert(auvJobLog);

            // 移除记录
            iterator.remove();
          }
        }

        // 处理超时任务
        for (Tuple<JobInfo, Future> jobFuture : jobFutures) {
          JobInfo jobInfo = jobFuture.getV1();

          LocalDateTime startTime = jobInfo.getStartTime();
          LocalDateTime now = LocalDateTime.now();
          Duration between = Duration.between(startTime, now);
          Long timeout = jobInfo.getTimeout();

          Future future = jobFuture.getV2();
          if (between.get(ChronoUnit.SECONDS) > timeout && !future.isDone()) {
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
