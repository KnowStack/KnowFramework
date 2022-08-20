package com.didiglobal.logi.job.core.monitor;

import com.didiglobal.logi.job.common.domain.LogITask;
import com.didiglobal.logi.job.core.task.TaskManager;
import com.didiglobal.logi.job.utils.ThreadUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple task monitor.
 *
 * @author ds
 */
@Service
public class SimpleTaskMonitor implements TaskMonitor {

    private static final ILog logger     = LogFactory.getLog(SimpleTaskMonitor.class);

    public static final long SCAN_INTERVAL_SLEEP_SECONDS = 10;
    public static final long INTERVAL_SECONDS = 1;

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
        // 设置为守护线程
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    @Override
    public void stop() {
        logger.info("class=SimpleTaskMonitor||method=stop||msg=task monitor stopByJobCode!");
        try {
            taskManager.stopAll();
            if (monitorThread != null && monitorThread.isAlive()) {
                monitorThread.interrupt();
            }
        } catch (Exception e) {
            logger.error("class=SimpleTaskMonitor||method=stop||msg=exception!", e);
        }
    }

    class TaskMonitorExecutor implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    logger.info("class=TaskMonitorExecutor||method=run||msg=fetch tasks at regular {}",
                            SCAN_INTERVAL_SLEEP_SECONDS);

                    List<LogITask> logITaskList = taskManager.nextTriggers(INTERVAL_SECONDS);

                    if (logITaskList == null || logITaskList.size() == 0) {
                        logger.info("class=TaskMonitorExecutor||method=run||msg=no tasks need run!");
                        ThreadUtil.sleep(INTERVAL_SECONDS, TimeUnit.SECONDS);
                        continue;
                    }

                    // 未到执行时间，等待
                    logger.info("class=TaskMonitorExecutor||method=run||msg=fetch tasks {}",
                            logITaskList.stream().map(LogITask::getTaskName).collect(Collectors.toList()));

                    Long firstFireTime = logITaskList.stream().findFirst().get().getNextFireTime().getTime();
                    Long nowTime = System.currentTimeMillis();
                    if (nowTime < firstFireTime) {
                        Long between = firstFireTime - nowTime;
                        ThreadUtil.sleep(between + 1, TimeUnit.MILLISECONDS);
                    }

                    logger.info("class=TaskMonitorExecutor||method=run||msg=start tasks={}, "
                                    + "firstFireTime={}, nowTime={}",
                            logITaskList.stream().map(LogITask::getTaskName).collect(Collectors.toList()),
                            firstFireTime, nowTime);

                    // 提交任务
                    taskManager.submit(logITaskList);
                } catch (Exception e) {
                    logger.error("class=TaskMonitorExecutor||method=run||msg=exception!", e);
                }

                // 每次扫描，间隔1s。为了线程终端创造条件
                ThreadUtil.sleep(SCAN_INTERVAL_SLEEP_SECONDS, TimeUnit.SECONDS);
            }
        }
    }
}
