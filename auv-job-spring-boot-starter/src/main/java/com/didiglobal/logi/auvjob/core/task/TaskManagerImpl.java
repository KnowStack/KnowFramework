package com.didiglobal.logi.auvjob.core.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.dto.TaskDto;
import com.didiglobal.logi.auvjob.common.enums.TaskStatusEnum;
import com.didiglobal.logi.auvjob.core.Consensual;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.mapper.AuvTaskMapper;
import com.didiglobal.logi.auvjob.utils.Assert;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * task manager impl.
 *
 * @author dengshan
 */
@Service
public class TaskManagerImpl implements TaskManager {
  private static final Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

  private static final long WAIT_INTERVAL_SECONDS = 10L;

  private JobManager jobManager;
  private Consensual consensual;
  private TaskLockService taskLockService;
  private AuvTaskMapper auvTaskMapper;

  /**
   * constructor.
   *
   * @param jobManager jobManager
   * @param consensual consensual
   * @param taskLockService taskLockService
   * @param auvTaskMapper auvTaskMapper
   */
  public TaskManagerImpl(JobManager jobManager, Consensual consensual,
                         TaskLockService taskLockService, AuvTaskMapper auvTaskMapper) {
    this.jobManager = jobManager;
    this.consensual = consensual;
    this.taskLockService = taskLockService;
    this.auvTaskMapper = auvTaskMapper;
  }

  @Override
  public boolean add(TaskDto taskDto) {
    return false;
  }

  @Override
  public TaskDto delete(String taskCode) {
    AuvTask auvTask = auvTaskMapper.selectById(taskCode);
    if (auvTask == null) {
      return null;
    }
    auvTaskMapper.deleteById(taskCode);
    return BeanUtil.convertTo(auvTask, TaskDto.class);
  }

  @Override
  public boolean update(TaskDto taskDto) {
    AuvTask auvTask = BeanUtil.convertTo(taskDto, AuvTask.class);
    return auvTaskMapper.updateById(auvTask) > 0 ? true : false;
  }

  @Override
  public List<TaskInfo> nextTriggers(Long interval, TemporalUnit timeUnit) {
    return nextTriggers(LocalDateTime.now(), interval, timeUnit);
  }

  @Override
  public List<TaskInfo> nextTriggers(LocalDateTime fromTime, Long interval, TemporalUnit timeUnit) {
    List<TaskInfo> taskInfoList = new ArrayList<>();
    List<AuvTask> auvTaskList = auvTaskMapper.selectList(new QueryWrapper<AuvTask>()
            .ne("status", TaskStatusEnum.STOPPED.getValue()));
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
      if (cronExpression == null) {
        return;
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
      execute(taskInfo, false);
    }
  }

  public void execute(String taskCode, Boolean executeSubs) {
    Assert.notNull(taskCode, "taskCode can not be null");
    AuvTask auvTask = auvTaskMapper.selectList(new QueryWrapper<AuvTask>().eq("code", taskCode)
            .eq("status", TaskStatusEnum.WAITING.getValue())).stream().findFirst().orElseGet(null);
    Assert.notNull(auvTask, "taskCode is invalid!");
    Assert.isTrue(taskLockService.tryAcquire(taskCode), "can not get lock, may be task is running.");

    /* 获取到锁 */
    // 更新任务状态，最近更新时间
    auvTaskMapper.update(null, new UpdateWrapper<AuvTask>()
            .eq("code", taskCode).set("status", TaskStatusEnum.RUNNING.getValue())
            .set("last_fire_time", LocalDateTime.now()));
    // 添加回调函数，执行
    TaskInfo taskInfo = BeanUtil.convertTo(auvTask, TaskInfo.class);
    taskInfo.setTaskCallback(code -> taskLockService.tryRelease(code));
    executeInternal(taskInfo, executeSubs);
  }

  @Override
  public void execute(TaskInfo taskInfo, Boolean executeSubs) {
    if (taskLockService.tryAcquire(taskInfo.getCode())) {
      // 更新任务状态，最近更新时间
      auvTaskMapper.update(null, new UpdateWrapper<AuvTask>()
              .eq("code", taskInfo.getCode()).set("status", TaskStatusEnum.RUNNING.getValue())
              .set("last_fire_time", LocalDateTime.now()));
      // 添加回调函数
      taskInfo.setTaskCallback(taskCode -> taskLockService.tryRelease(taskCode));
      // 执行
      executeInternal(taskInfo, executeSubs);
    }
  }

  @Override
  public boolean pause(String taskCode) {
    return auvTaskMapper.update(null, new UpdateWrapper<AuvTask>()
            .eq("code", taskCode)
            .set("status", TaskStatusEnum.STOPPED.getValue())) > 0;
  }

  @Override
  public int pauseAll() {
    return auvTaskMapper.update(null, new UpdateWrapper<AuvTask>()
            .eq("status", TaskStatusEnum.WAITING.getValue())
            .set("status", TaskStatusEnum.STOPPED.getValue()));
  }

  @Override
  public boolean resume(String taskCode) {
    return auvTaskMapper.update(null, new UpdateWrapper<AuvTask>()
            .eq("code", taskCode)
            .set("status", TaskStatusEnum.WAITING.getValue())) > 0;
  }

  @Override
  public int resumeAll() {
    return auvTaskMapper.update(null, new UpdateWrapper<AuvTask>()
            .eq("status", TaskStatusEnum.WAITING.getValue())
            .set("status", TaskStatusEnum.WAITING.getValue()));
  }

  // ################################## private method #######################################

  private void executeInternal(TaskInfo taskInfo, Boolean executeSubs) {
    // jobManager 将job管理起来，超时退出抛异常
    final Future<Object> jobFuture = jobManager.start(taskInfo);
    if (jobFuture == null || !executeSubs) {
      return;
    }
    // 等待父任务运行完
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
}
