package com.didiglobal.logi.auvjob.core;

import com.didiglobal.logi.auvjob.core.monitor.BeatMonitor;
import com.didiglobal.logi.auvjob.core.monitor.MisfireMonitor;
import com.didiglobal.logi.auvjob.core.monitor.TaskMonitor;

/**
 * 默认worker实现
 * @author dengshan
 */
public class SimpleWorker implements Worker {

  /**
   * 心跳执行器
   */
  private BeatMonitor beatMonitor;

  /**
   * 任务执行器
   */
  private TaskMonitor taskMonitor;

  /**
   * 错失任务执行器
   */
  private MisfireMonitor misfireMonitor;

  public SimpleWorker(BeatMonitor beatMonitor, TaskMonitor taskMonitor, MisfireMonitor
      misfireMonitor) {
    this.beatMonitor = beatMonitor;
    this.taskMonitor = taskMonitor;
    this.misfireMonitor = misfireMonitor;
  }

  @Override
  public void initialize() {
    beatMonitor.maintain();
  }

  @Override
  public boolean startup() {
    taskMonitor.maintain();
    misfireMonitor.maintain();

    // todo 先返回false，考虑根据实际调整
    return false;
  }

  @Override
  public boolean shutdown() {
    beatMonitor.stop();
    taskMonitor.stop();
    misfireMonitor.stop();

    // todo 先返回false，考虑根据实际调整
    return false;
  }
}
