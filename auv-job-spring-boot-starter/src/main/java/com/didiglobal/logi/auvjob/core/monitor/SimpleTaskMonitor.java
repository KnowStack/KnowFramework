package com.didiglobal.logi.auvjob.core.monitor;

import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple task monitor.
 *
 * @author dengshan
 */
@Service
public class SimpleTaskMonitor implements TaskMonitor {

  private static final Logger logger = LoggerFactory.getLogger(SimpleTaskMonitor.class);

  /*
   * 任务管理器
   */
  private TaskManager taskManager;

  /*
   * 任务监听器执行线程
   */
  private Thread monitorThread;

  @Autowired
  public SimpleTaskMonitor(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  @Override
  public void maintain() {
    monitorThread = new Thread(new TaskMonitorExecutor(), "TaskMonitorExecutor_Thread");
    monitorThread.start();
  }

  @Override
  public void stop() {
    if (monitorThread != null && monitorThread.isAlive()) {
      monitorThread.interrupt();
    }
  }

  class TaskMonitorExecutor implements Runnable {
    private static final long INTERVAL_SECONDS = 10;

    @Override
    public void run() {
      while (true) {
        List<TaskInfo> taskInfoList = taskManager.nextTriggers(INTERVAL_SECONDS);

        if (taskInfoList == null || taskInfoList.size() == 0) {
          ThreadUtil.sleep(INTERVAL_SECONDS, TimeUnit.SECONDS);
          continue;
        }

        // 未到执行时间，等待
        Long firstFireTime = taskInfoList.stream().findFirst().get().getNextFireTime().getTime();
        Long nowTime = System.currentTimeMillis();
        if (nowTime < firstFireTime) {
          Long between = firstFireTime - nowTime;
          ThreadUtil.sleep(between / 1000, TimeUnit.SECONDS);
        }

        // 提交任务
        taskManager.submit(taskInfoList);
      }
    }
  }
}
