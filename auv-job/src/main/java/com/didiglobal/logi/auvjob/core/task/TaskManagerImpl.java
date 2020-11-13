package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.bean.TaskInfo;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobFactory;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.mapper.TaskInfoMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author dengshan
 */
public class TaskManagerImpl implements TaskManager {

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
  public boolean execute(long taskId) {
    return false;
  }

  @Override
  public boolean execute(TaskInfo taskInfo) {
    final Job job = jobFactory.newJob(taskInfo);
    jobManager.start(job);
    return false;
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
