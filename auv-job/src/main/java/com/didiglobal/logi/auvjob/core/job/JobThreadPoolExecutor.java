package com.didiglobal.logi.auvjob.core.job;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dengshan
 */
public class JobThreadPoolExecutor {

  ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
      1000L, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(100));

  <T> Future<T> submmit(Callable<T> task) {
    return threadPoolExecutor.submit(task);
  }

}
