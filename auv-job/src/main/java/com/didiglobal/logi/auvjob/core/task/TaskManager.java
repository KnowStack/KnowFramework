package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.bean.TaskInfo;

import java.util.List;

/**
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
   * 执行任务 todo 名字不一定合适，考虑修改
   * @param taskId
   * @return
   */
  boolean execute(long taskId);

  /**
   * 执行任务 todo 同样考虑修改名字
   * @param taskInfo
   * @return
   */
  boolean execute(TaskInfo taskInfo);

  boolean pause(long taskId);

  int pauseAll(long taskId);

}
