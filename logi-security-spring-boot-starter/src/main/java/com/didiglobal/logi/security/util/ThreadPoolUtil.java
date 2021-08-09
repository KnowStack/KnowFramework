package com.didiglobal.logi.security.util;

import java.util.concurrent.*;

/**
 * @author cjm
 */
public class ThreadPoolUtil {

    public static ThreadPoolExecutor threadPool;

    /**
     * 无返回值直接执行
     *
     * @param runnable 执行的方法
     */
    public static void execute(Runnable runnable) {
        getThreadPool().execute(runnable);
    }

    /**
     * 返回值直接执行
     *
     * @param callable 回调
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        getThreadPool().shutdown();
    }

    /**
     * dcs获取线程池
     *
     * @return 线程池对象
     */
    private static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) {
            return threadPool;
        } else {
            synchronized (ThreadPoolUtil.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(
                            2, 4, 60, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(32), new ThreadPoolExecutor.CallerRunsPolicy()
                    );
                }
                return threadPool;
            }
        }
    }

}
