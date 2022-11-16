package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.LogIJobProperties;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * job thread pool executor.
 *
 * @author ds
 */
@Component
public class JobExecutor {

    public ThreadPoolExecutor threadPoolExecutor;

    private volatile Long rejectedTaskNumber;

    /**
     * construct
     * @param properties 配置信息
     */
    @Autowired
    public JobExecutor(LogIJobProperties properties) {
        this.threadPoolExecutor = new ThreadPoolExecutor(properties.getInitThreadNum(),
                properties.getMaxThreadNum(), 10L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
        this.rejectedTaskNumber = 0L;
    }

    public <T> Future<T> submit(Callable<T> task) {
        try {
            return threadPoolExecutor.submit(task);
        } catch (RejectedExecutionException ex) {
            rejectedTaskNumber++;
            throw ex;
        }
    }

    public Long getRejectedTaskNumber() {
        return rejectedTaskNumber;
    }

    public Integer getThreadPoolQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }

    public Integer getThreadPoolThreadMaxSize() {
        return threadPoolExecutor.getMaximumPoolSize();
    }

}
