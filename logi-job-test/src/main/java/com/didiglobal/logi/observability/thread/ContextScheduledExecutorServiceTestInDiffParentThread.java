package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.ContextScheduledFuture;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.SneakyThrows;

import java.util.concurrent.*;

public class ContextScheduledExecutorServiceTestInDiffParentThread {

    private static Tracer tracer = Observability.getTracer(ContextScheduledExecutorServiceTestInDiffParentThread.class.getName());

    public static void main(String[] args) throws InterruptedException {

        //1.）封装线程 池
        ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));

        ScheduledFuture<String> scheduledFuture = null;
        Span span = tracer.spanBuilder("main").startSpan();
        try (Scope scope = span.makeCurrent()) {
            System.out.println("start function main()");
            //2.）提交附带返回值任务
            scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
        } finally {
            span.end();
        }

        //3.）将范围值作为入参，新线程执行
        threadPool2.scheduleWithFixedDelay(new MyRunnable(scheduledFuture),10, 10, TimeUnit.SECONDS);

        Thread.sleep(1000 * 60 * 4);

    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("MyCallable.call()");
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
                ContextScheduledFuture contextFuture = (ContextScheduledFuture) scheduledFuture;
                String msg = contextFuture.get().toString();
                contextFuture.getContext().makeCurrent();
                Span span = tracer.spanBuilder("MyRunnable.run()").startSpan();
                try(Scope scope = span.makeCurrent()) {
                    System.out.println("MyRunnable.run()");
                    System.out.println(" parameter is : " + msg);
                } finally {
                    span.end();
                }
            } catch (Exception ex) {

            }
        }
    }

}
