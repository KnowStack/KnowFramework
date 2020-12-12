package com.didiglobal.logi.auvjob.core.job;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 * job thread pool executor.
 *
 * @author dengshan
 */
public class JobThreadPoolExecutor {

  public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
      10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

  public static <T> Future<T> submit(Callable<T> task) {
    return threadPoolExecutor.submit(task);
  }

}
