package com.didiglobal.logi.auvjob.core.monitor;

import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple task monitor.
 *
 * @author dengshan
 */
public class SimpleTaskMonitor implements TaskMonitor {

  private static final Logger logger = LoggerFactory.getLogger(SimpleTaskMonitor.class);

  /*
   * 任务管理器
   */
  private TaskManager taskManager;

  /*
   * 任务监听器执行线程
   */
  private Thread taskMonitorThread;

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

  class TaskMonitorExecutor implements Runnable {
    private static final long INTERVAL_SECONDS = 10;

    @Override
    public void run() {
      while (true) {
        List<TaskInfo> taskInfoList = taskManager.nextTriggers(INTERVAL_SECONDS,
                ChronoUnit.SECONDS);

        if (taskInfoList == null || taskInfoList.size() == 0) {
          ThreadUtil.sleep(INTERVAL_SECONDS, TimeUnit.MICROSECONDS);
          continue;
        }

        // 未到执行时间，等待
        LocalDateTime firstFireTime = taskInfoList.stream().findFirst().get().getNextFireTime();
        if (LocalDateTime.now().isBefore(firstFireTime)) {
          Duration between = Duration.between(LocalDateTime.now(), firstFireTime);
          ThreadUtil.sleep(between.getSeconds(), TimeUnit.SECONDS);
        }

        // 提交任务
        taskManager.submit(taskInfoList);
      }
    }
  }
}
