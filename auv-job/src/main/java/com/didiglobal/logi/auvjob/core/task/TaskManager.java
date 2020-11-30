package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * 任务的CRUD及执行管控.
 *
 * @author dengshan
 */
public interface TaskManager {

  /**
   * 新增任务.
   *
   * @param auvTask todo 参数待定
   * @return true/false
   */
  boolean add(AuvTask auvTask);

  /**
   * 更新任务.
   *
   * @param taskId todo 返回值待定
   * @return deleted auv task
   */
  AuvTask delete(long taskId);

  /**
   * 更新任务.
   *
   * @param auvTask todo 参数待定
   * @return true/false
   */
  boolean update(AuvTask auvTask);

  /**
   * 接下来需要执行的任务,按时间先后顺序排序.
   *
   * @param interval 从现在开始下次执行时间间隔
   * @param timeUnit 时间单位
   * @return task info list
   */
  List<TaskInfo> nextTriggers(Long interval, TemporalUnit timeUnit);

  /**
   * 接下来需要执行的任务,按时间先后顺序排序.
   *
   * @param fromTime 开始时间
   * @param interval 从指定开始时间，下次执行时间间隔
   * @param timeUnit 时间单位
   * @return task info list
   */
  List<TaskInfo> nextTriggers(LocalDateTime fromTime, Long interval, TemporalUnit timeUnit);

  /**
   * 提交任务，执行器会根据一致性协同算法判断是否执行.
   *
   * @param taskInfoList task info list
   */
  void submit(List<TaskInfo> taskInfoList);

  /**
   * 执行任务, 默认会执行子任务如果有配置.
   *
   * @param taskInfo task info
   */
  void execute(TaskInfo taskInfo);

  /**
   * 执行任务.
   *
   * @param taskInfo 任务信息
   * @param executeSubs 是否执行子任务
   */
  void execute(TaskInfo taskInfo, Boolean executeSubs);

  boolean pause(long taskId);

  int pauseAll(long taskId);

}
