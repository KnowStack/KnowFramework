package com.didiglobal.logi.auvjob.core;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
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
import com.didiglobal.logi.auvjob.mapper.JobInfoMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * worker创建工厂
 * @author dengshan
 */
public class SimpleWorkerFactory implements WorkerFactory {

  @Override
  public Worker instantiate() {
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

    // instantiate threadPool,job,task
    JobThreadPoolExecutor threadPoolExecutor = new JobThreadPoolExecutor();
    JobManager jobManager = new JobManagerImpl(threadPoolExecutor, sqlSession);
    JobFactory jobFactory = new SimpleJobFactory();
    TaskManager taskManager = new TaskManagerImpl(jobManager, jobFactory, sqlSession);
    Consensual consensual = new RandomConsensual();
    TaskMonitor taskMonitor = new SimpleTaskMonitor(taskManager);
    taskMonitor.setConsensual(consensual);

    BeatMonitor beatMonitor = new SimpleBeatMonitor();
    MisfireMonitor misfireMonitor = new SimpleMisfireMonitor();

    Worker worker = new SimpleWorker(beatMonitor, taskMonitor, misfireMonitor);
    worker.initialize();
    return worker;
  }

  @Override
  public Worker getWorker() {
    return null;
  }
}
