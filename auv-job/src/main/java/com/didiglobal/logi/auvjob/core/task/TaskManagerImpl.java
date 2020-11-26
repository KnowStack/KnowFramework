package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.bean.TaskInfo;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobFactory;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.mapper.TaskInfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author dengshan
 */
public class TaskManagerImpl implements TaskManager {
  private static final Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

  private JobManager jobManager;
  private JobFactory jobFactory;
  private SqlSession sqlSession;

  private TaskInfoMapper taskInfoMapper;

  public TaskManagerImpl(JobManager jobManager, JobFactory jobFactory, SqlSession sqlSession) {
    this.jobManager = jobManager;
    this.jobFactory = jobFactory;
    this.sqlSession = sqlSession;
    initialize();
  }

  private void initialize() {
    this.taskInfoMapper = sqlSession.getMapper(TaskInfoMapper.class);
  }


  @Override
  public boolean add(TaskInfo taskInfo) {
    return false;
  }

  @Override
  public TaskInfo delete(long taskId) {
    return null;
  }

  @Override
  public boolean update(TaskInfo taskInfo) {
    return false;
  }

  @Override
  public List<TaskInfo> nextTriggers(long intervalTime) {
    return null;
  }

  @Override
  public void execute(TaskInfo taskInfo) {
    execute(taskInfo, false);
  }

  @Override
  public void execute(TaskInfo taskInfo, Boolean executeSubs) {
    final Job job = jobFactory.newJob(taskInfo);
    // jobManager 将job管理起来，超时时推出跑异常
    final Future<Object> jobFuture = jobManager.start(job);
    if (executeSubs) {
      try {
        jobFuture.get(taskInfo.getTimeout(), TimeUnit.NANOSECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.error("", e);
      }
    }
  }

  @Override
  public boolean pause(long taskId) {
    return false;
  }

  @Override
  public int pauseAll(long taskId) {
    return 0;
  }
}
