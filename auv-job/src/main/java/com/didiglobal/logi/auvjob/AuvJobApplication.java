package com.didiglobal.logi.auvjob;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.core.Consensual;
import com.didiglobal.logi.auvjob.core.RandomConsensual;
import com.didiglobal.logi.auvjob.core.Scheduler;
import com.didiglobal.logi.auvjob.core.SimpleScheduler;
import com.didiglobal.logi.auvjob.core.SimpleWorkerFactory;
import com.didiglobal.logi.auvjob.core.TaskLockService;
import com.didiglobal.logi.auvjob.core.TaskLockServiceImpl;
import com.didiglobal.logi.auvjob.core.WorkerFactory;
import com.didiglobal.logi.auvjob.core.job.JobFactory;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.core.job.JobManagerImpl;
import com.didiglobal.logi.auvjob.core.job.JobThreadPoolExecutor;
import com.didiglobal.logi.auvjob.core.job.SimpleJobFactory;
import com.didiglobal.logi.auvjob.core.monitor.BeatMonitor;
import com.didiglobal.logi.auvjob.core.monitor.MisfireMonitor;
import com.didiglobal.logi.auvjob.core.monitor.SimpleBeatMonitor;
import com.didiglobal.logi.auvjob.core.monitor.SimpleMisfireMonitor;
import com.didiglobal.logi.auvjob.core.monitor.SimpleTaskMonitor;
import com.didiglobal.logi.auvjob.core.monitor.TaskMonitor;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import com.didiglobal.logi.auvjob.core.task.TaskManagerImpl;
import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


public class AuvJobApplication {

  /**
   * 入口函数.
   *
   * @param args 参数
   */
  public static void main(String[] args) {
    // todo 剥离mybatis的初始化过程，引入连接池
    InputStream inputStream = null;
    try {
      inputStream = Resources.getResourceAsStream("mybatis.cfg.xml");
    } catch (IOException e) {
      // todo
      e.printStackTrace();
    }
    MybatisSqlSessionFactoryBuilder sessionFactoryBuilder = new MybatisSqlSessionFactoryBuilder();
    SqlSessionFactory sessionFactory = sessionFactoryBuilder.build(inputStream);
    SqlSession sqlSession = sessionFactory.openSession();

    //
    JobThreadPoolExecutor threadPoolExecutor = new JobThreadPoolExecutor();
    JobFactory jobFactory = new SimpleJobFactory();
    JobManager jobManager = new JobManagerImpl(threadPoolExecutor, sqlSession, jobFactory);

    Consensual consensual = new RandomConsensual();
    WorkerFactory workerFactory = new SimpleWorkerFactory();
    WorkerInfo workerInfo = workerFactory.newWorker();
    TaskLockService taskLockService = new TaskLockServiceImpl(sqlSession, workerInfo);
    TaskManager taskManager = new TaskManagerImpl(jobManager, sqlSession, consensual,
            taskLockService);

    TaskMonitor taskMonitor = new SimpleTaskMonitor(taskManager);
    BeatMonitor beatMonitor = new SimpleBeatMonitor();
    MisfireMonitor misfireMonitor = new SimpleMisfireMonitor();

    Scheduler scheduler = new SimpleScheduler(beatMonitor, taskMonitor, misfireMonitor);
    scheduler.startup();
  }
}
