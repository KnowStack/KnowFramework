package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.dto.TaskDto;
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
  boolean add(TaskDto taskDto);

  /**
   * 更新任务.
   *
   * @param taskCode task code
   * @return deleted auv task
   */
  TaskDto delete(String taskCode);

  /**
   * 更新任务.
   *
   * @param taskDto
   * @return true/false
   */
  boolean update(TaskDto taskDto);

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
   * 根据 task code 执行任务
   *
   * @param taskCode task code
   * @param executeSubs 是否执行子任务
   * @return
   */
  void execute(String taskCode, Boolean executeSubs);

  /**
   * 执行任务, 默认会执行子任务如果有配置.
   *
   * @param taskInfo 任务信息
   * @param executeSubs 是否执行子任务
   */
  void execute(TaskInfo taskInfo, Boolean executeSubs);

  boolean pause(String taskCode);

  int pauseAll();

  boolean resume(String taskCode);

  int resumeAll();

}
