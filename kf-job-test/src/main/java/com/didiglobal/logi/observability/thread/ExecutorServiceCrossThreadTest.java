package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.CrossThreadRunnableWithContextFuture;
import io.opentelemetry.api.trace.Tracer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.*;

public class ExecutorServiceCrossThreadTest {

    private static Tracer tracer = Observability.getTracer(ExecutorServiceCrossThreadTest.class.getName());
    private static final ILog logger = LogFactory.getLog(ExecutorServiceCrossThreadTest.class);

    public static void main(String[] args) throws InterruptedException {
        /*
         * 1.封装线程池
         */
        ExecutorService threadPool1 = Observability.wrap(
                new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>( 100 ),
                        new BasicThreadFactory.Builder().namingPattern("main-1").build())
        );
        ExecutorService threadPool2 = Observability.wrap(
                new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>( 100 ),
                        new BasicThreadFactory.Builder().namingPattern("main-2").build())
        );
        logger.info("start function main()");
        /*
         * 2.提交附带返回值任务
         */
        Future<String> future = threadPool1.submit(new MyCallable());
        /*
         * 3.将返回值作为入参，交付新线程进行执行
         */
        threadPool2.submit(new MyRunnable(future));

        Thread.sleep(1000 * 60 * 4);
    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            logger.info("MyCallable.call()");
            return "SUCCESSFUL";
        }
    }

    static class MyRunnable extends CrossThreadRunnableWithContextFuture {

        public MyRunnable(Future future) {
            super(future);
        }

        @SneakyThrows
        @Override
        public void run() {
            logger.info("MyRunnable.run()");
            logger.info(" parameter is : " + super.getFuture().get().toString());
        }
    }

}
