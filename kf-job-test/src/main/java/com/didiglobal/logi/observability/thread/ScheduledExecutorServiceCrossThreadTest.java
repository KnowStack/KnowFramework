package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.CrossThreadRunnableWithContextScheduledFuture;
import io.opentelemetry.api.trace.Tracer;
import lombok.SneakyThrows;

import java.util.concurrent.*;

public class ScheduledExecutorServiceCrossThreadTest {

    private static Tracer tracer = Observability.getTracer(ScheduledExecutorServiceCrossThreadTest.class.getName());
    private static final ILog logger = LogFactory.getLog(ScheduledExecutorServiceCrossThreadTest.class);

    public static void main(String[] args) throws InterruptedException {

        /*
         * 1.封装线程池
         */
        ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledFuture<String> scheduledFuture = null;
        logger.info("start function main()");
        /*
         * 2.提交附带返回值任务
         */
        scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
        /*
         * 3.将返回值作为入参，交付新线程进行执行
         */
        threadPool2.scheduleWithFixedDelay(new MyRunnable(scheduledFuture),10, 10, TimeUnit.SECONDS);

        Thread.sleep(1000 * 60 * 4);
    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            logger.info("MyCallable.call()");
            return "SUCCESSFUL";
        }
    }

    static class MyRunnable extends CrossThreadRunnableWithContextScheduledFuture {

        public MyRunnable(ScheduledFuture scheduledFuture) {
            super(scheduledFuture);
        }

        @SneakyThrows
        @Override
        public void run() {
            logger.info("MyRunnable.run()");
            logger.info(" parameter is : " + super.getScheduledFuture().get().toString());
        }

    }

}
