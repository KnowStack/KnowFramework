package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import io.opentelemetry.api.trace.Tracer;
import lombok.SneakyThrows;

import java.util.concurrent.*;

public class ScheduledExecutorServiceNonCrossThreadTest {

    private static Tracer tracer = Observability.getTracer(ScheduledExecutorServiceNonCrossThreadTest.class.getName());
    private static final ILog logger = LogFactory.getLog(ScheduledExecutorServiceNonCrossThreadTest.class);

    public static void main(String[] args) throws InterruptedException {

        /*
         * 1.封装线程池
         */
        ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));
        logger.info("start function main()");
        /*
         * 2.提交附带返回值任务
         */
        ScheduledFuture<String> scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
        /*
         * 3.将返回值作为入参，交付新线程进行执行
         */
        threadPool2.scheduleWithFixedDelay(new MyRunnable(scheduledFuture),10, 5 * 1000 * 60, TimeUnit.SECONDS);

        Thread.sleep(1000 * 60 * 4);
    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            logger.info("MyCallable.call()");
            return "SUCCESSFUL";
        }
    }

    static class MyRunnable implements Runnable {
        private ScheduledFuture scheduledFuture;
        public MyRunnable(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
        @SneakyThrows
        @Override
        public void run() {
            try {
                String msg = scheduledFuture.get().toString();
                logger.info("MyRunnable.run()");
                logger.info(" parameter is : " + msg);
            } catch (Exception ex) {

            }
        }
    }

}
