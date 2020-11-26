package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.bean.TaskInfo;

import java.util.List;

/**
 * 任务的CRUD及执行管控
 * @author dengshan
 */
public interface TaskManager {

  /**
   * 新增任务
   * @param taskInfo todo 参数待定
   * @return
   */
  boolean add(TaskInfo taskInfo);

  /**
   * 更新任务
   * @param taskId todo 返回值待定
   * @return
   */
  TaskInfo delete(long taskId);

  /**
   * 更新任务
   * @param taskInfo todo 参数待定
   * @return
   */
  boolean update(TaskInfo taskInfo);

  /**
   * 接下来需要执行的任务,按时间先后顺序排序
   *
   * @param intervalTime
   * @return
   */
  List<TaskInfo> nextTriggers(long intervalTime);

  /**
   * 执行任务, 默认会执行子任务如果有配置
   * @param taskInfo
   * @return
   */
  void execute(TaskInfo taskInfo);

  /**
   * 执行任务
   * @param taskInfo 任务信息
   * @param executeSubs 是否执行子任务
   * @return
   */
  void execute(TaskInfo taskInfo, Boolean executeSubs);

  boolean pause(long taskId);

  int pauseAll(long taskId);

}
