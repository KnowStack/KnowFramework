package com.didiglobal.logi.auvjob.core.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.enums.TaskStatusEnum;
import com.didiglobal.logi.auvjob.core.Consensual;
import com.didiglobal.logi.auvjob.core.TaskLockService;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.mapper.AuvTaskMapper;
import com.didiglobal.logi.auvjob.utils.BeanUtil;
import com.didiglobal.logi.auvjob.utils.CronExpression;
import com.didiglobal.logi.auvjob.utils.DateUtil;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * task manager impl.
 *
 * @author dengshan
 */
public class TaskManagerImpl implements TaskManager {
  private static final Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

  private static final long WAIT_INTERVAL_SECONDS = 10L;

  private JobManager jobManager;
  private SqlSession sqlSession;
  private Consensual consensual;
  private TaskLockService taskLockService;

  private AuvTaskMapper auvTaskMapper;

  /**
   * constructor.
   *
   * @param jobManager job manager
   * @param sqlSession sql session
   * @param consensual consensual
   * @param taskLockService task lock service
   */
  public TaskManagerImpl(JobManager jobManager, SqlSession sqlSession, Consensual consensual,
                         TaskLockService taskLockService) {
    this.jobManager = jobManager;
    this.sqlSession = sqlSession;
    this.consensual = consensual;
    this.taskLockService = taskLockService;
    this.auvTaskMapper = sqlSession.getMapper(AuvTaskMapper.class);
  }

  @Override
  public boolean add(AuvTask auvTask) {
    return false;
  }

  @Override
  public AuvTask delete(long taskId) {
    return null;
  }

  @Override
  public boolean update(AuvTask auvTask) {
    return false;
  }

  @Override
  public List<TaskInfo> nextTriggers(Long interval, TemporalUnit timeUnit) {
    return nextTriggers(LocalDateTime.now(), interval, timeUnit);
  }

  @Override
  public List<TaskInfo> nextTriggers(LocalDateTime fromTime, Long interval, TemporalUnit timeUnit) {
    List<TaskInfo> taskInfoList = new ArrayList<>();
    List<AuvTask> auvTaskList = auvTaskMapper.selectList(new QueryWrapper<AuvTask>()
            .ne("status", TaskStatusEnum.STOPPED));
    if (CollectionUtils.isEmpty(auvTaskList)) {
      return taskInfoList;
    }

    // 转taskInfo
    taskInfoList = auvTaskList.stream().map(auvTask -> BeanUtil.convertTo(auvTask, TaskInfo.class))
            .collect(Collectors.toList());
    taskInfoList.forEach(taskInfo -> {
      CronExpression cronExpression = null;
      try {
        cronExpression = new CronExpression(taskInfo.getCron());
      } catch (Exception e) {
        logger.error("class=TaskManagerImpl||method=nextTrigger||url=||msg={}", e);
      }
      Date nextTime = cronExpression.getNextValidTimeAfter(DateUtil.toDate(taskInfo
              .getLastFireTime()));
      taskInfo.setNextFireTime(DateUtil.toLocalDateTime(nextTime));
    });
    // filter
    taskInfoList = taskInfoList.stream().filter(taskInfo -> fromTime.plus(interval, timeUnit)
            .isAfter(taskInfo.getNextFireTime())).collect(Collectors.toList());
    // sort
    taskInfoList.sort(Comparator.comparing(TaskInfo::getNextFireTime));
    return taskInfoList;
  }

  @Override
  public void submit(List<TaskInfo> taskInfoList) {
    if (CollectionUtils.isEmpty(taskInfoList)) {
      return;
    }
    for (TaskInfo taskInfo : taskInfoList) {
      // 不能在本工作器执行，跳过
      if (!consensual.canClaim(taskInfo)) {
        continue;
      }

      // 尝试抢占锁,没有获取证明其他机器已经获取并执行了
      if (taskLockService.tryAcquire(taskInfo.getCode())) {
        try {
          execute(taskInfo);
        } catch (Exception e) {
          logger.error("class=TaskManagerImpl||method=submit||url=||msg={}", e);
        } finally {
          taskLockService.tryRelease(taskInfo.getCode());
        }
      }
    }
  }

  @Override
  public void execute(TaskInfo taskInfo) {
    execute(taskInfo, false);
  }

  @Override
  public void execute(TaskInfo taskInfo, Boolean executeSubs) {
    // jobManager 将job管理起来，超时退出抛异常
    final Future<Object> jobFuture = jobManager.start(taskInfo);
    if (!executeSubs) {
      return;
    }
    // 等待任务运行完
    while (!jobFuture.isDone()) {
      ThreadUtil.sleep(WAIT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
    // 递归拉起子任务
    if (StringUtils.isNotEmpty(taskInfo.getSubTaskCodes())) {
      String[] subTaskCodeArray = taskInfo.getSubTaskCodes().split(",");
      List<AuvTask> subTasks = auvTaskMapper.selectBatchIds(Arrays.asList(subTaskCodeArray));
      List<TaskInfo> subTaskInfoList = subTasks.stream().map(auvTask -> BeanUtil.convertTo(auvTask,
              TaskInfo.class)).collect(Collectors.toList());
      for (TaskInfo subTaskInfo : subTaskInfoList) {
        execute(subTaskInfo, executeSubs);
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
