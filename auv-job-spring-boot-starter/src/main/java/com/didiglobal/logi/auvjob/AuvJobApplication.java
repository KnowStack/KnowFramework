package com.didiglobal.logi.auvjob;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.core.SimpleScheduler;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.core.monitor.BeatMonitor;
import com.didiglobal.logi.auvjob.core.monitor.MisfireMonitor;
import com.didiglobal.logi.auvjob.core.monitor.TaskMonitor;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages="com.didiglobal.logi.auvjob")
public class AuvJobApplication {

  /**
   * 入口函数.
   *
   * @param args 参数
   */
  public static void main(String[] args) {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AuvJobApplication.class);
    BeatMonitor beatMonitor = applicationContext.getBean(BeatMonitor.class);
    TaskMonitor taskMonitor = applicationContext.getBean(TaskMonitor.class);
    MisfireMonitor misfireMonitor = applicationContext.getBean(MisfireMonitor.class);
    SimpleScheduler simpleScheduler = new SimpleScheduler(beatMonitor, taskMonitor, misfireMonitor);
    simpleScheduler.startup();

    AuvTaskLockMapper auvTaskLockMapper = applicationContext.getBean(AuvTaskLockMapper.class);
    Runtime.getRuntime().addShutdownHook(new Thread(new AuvJobShutdownHook(auvTaskLockMapper),
            "AuvJobShutdownHook_Thread"));
  }

  static class AuvJobShutdownHook implements Runnable {

    private AuvTaskLockMapper auvTaskLockMapper;

    public AuvJobShutdownHook(AuvTaskLockMapper auvTaskLockMapper) {
      this.auvTaskLockMapper = auvTaskLockMapper;
    }

    @Override
    public void run() {
      WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
      WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
      auvTaskLockMapper.delete(new QueryWrapper<AuvTaskLock>().eq("worker_code",
              workerInfo.getCode()));

    }
  }
}
