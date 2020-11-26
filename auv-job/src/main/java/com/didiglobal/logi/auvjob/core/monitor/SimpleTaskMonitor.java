package com.didiglobal.logi.auvjob.core.monitor;

import com.didiglobal.logi.auvjob.common.bean.TaskInfo;
import com.didiglobal.logi.auvjob.core.Consensual;
import com.didiglobal.logi.auvjob.core.TaskLockService;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dengshan
 */
public class SimpleTaskMonitor implements TaskMonitor {

  private static final long INTERVAL_TIME = 500L;
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTaskMonitor.class);

  /**
   * 执行判断器
   */
  private Consensual consensual;

  /**
   * 任务管理器
   */
  private TaskManager taskManager;

  /**
   * 任务监听器执行线程
   */
  private Thread taskMonitorThread;

  /**
   * 任务锁
   */
  private TaskLockService taskLockService;

  public SimpleTaskMonitor(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  @Override
  public void maintain() {
    taskMonitorThread = new Thread(new TaskMonitorExecutor());
    taskMonitorThread.start();
  }

  @Override
  public void stop() {
    if (taskMonitorThread != null && taskMonitorThread.isAlive()) {
      taskMonitorThread.interrupt();
    }
  }

  @Override
  public void setConsensual(Consensual consensual) {
    this.consensual = consensual;
  }

  class TaskMonitorExecutor implements Runnable {

    @Override
    public void run() {
      while (true) {
        List<TaskInfo> taskInfoList = taskManager.nextTriggers(INTERVAL_TIME);
        try {

          if (taskInfoList == null || taskInfoList.size() == 0) {
            TimeUnit.MICROSECONDS.sleep(INTERVAL_TIME);
            continue;
          }

          for (TaskInfo taskInfo : taskInfoList) {
            // 不能在本工作器执行，跳过
            if (!consensual.canClaim(taskInfo)) {
              continue;
            }

            // 未到执行时间，等待
            final long executeTime = taskInfo.getNextFireTime().getTime();
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < executeTime) {
              TimeUnit.MILLISECONDS.sleep(executeTime - currentTimeMillis);
            }

            // 尝试抢占锁,没有获取证明其他机器已经获取并执行了
            if (!taskLockService.tryAcquire()) {
              continue;
            }
            // 执行任务
            taskManager.execute(taskInfo);
            // todo 执行子任务

            //
          }

        } catch (InterruptedException e) {
          // todo 考虑有没有必要做其他操作
          LOGGER.error("", e);
        }
      }
    }
  }
}
// tryAcquire
// tryRelease
