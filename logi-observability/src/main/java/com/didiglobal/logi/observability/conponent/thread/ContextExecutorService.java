package com.didiglobal.logi.observability.conponent.thread;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class ContextExecutorService implements ExecutorService {

    private final ExecutorService delegate;

    ExecutorService delegate() {
        return this.delegate;
    }

    public ContextExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void shutdown() {
        this.delegate().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.delegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate().isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Context context = Context.current();
        Future<T> future = this.delegate().submit(wrap(task, context));
        return new ContextFuture(future, context);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Context context = Context.current();
        Future<T> future = this.delegate().submit(wrap(task, context), result);
        return new ContextFuture(future, context);
    }

    @Override
    public Future<?> submit(Runnable task) {
        Context context = Context.current();
        Future<?> future = this.delegate().submit(wrap(task, context));
        return new ContextFuture(future, context);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        Context context = Context.current();
        List<Future<T>> futureList = this.delegate().invokeAll(wrap(context, tasks));
        List<Future<T>> contextFutureList = new ArrayList<>(futureList.size());
        for (Future<T> future : futureList) {
            contextFutureList.add(new ContextFuture<>(future, context));
        }
        return contextFutureList;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        Context context = Context.current();
        List<Future<T>> futureList = this.delegate().invokeAll(wrap(context, tasks), timeout, unit);
        List<Future<T>> contextFutureList = new ArrayList<>(futureList.size());
        for (Future<T> future : futureList) {
            contextFutureList.add(new ContextFuture<>(future, context));
        }
        return contextFutureList;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate().invokeAny(wrap(Context.current(), tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate().invokeAny(wrap(Context.current(), tasks), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.delegate().execute(wrap(command, Context.current()));
    }

    protected  <T> Callable<T> wrap(Callable<T> callable, Context context) {
        return () -> {
            try (Scope scope = context.makeCurrent()) {
                T value = callable.call();
                return value;
            } finally {
                //do nothing.
            }
        };
    }

    protected Runnable wrap(Runnable runnable, Context context) {
        return () -> {
            try (Scope scope = context.makeCurrent()) {
                runnable.run();
            } finally {
                //do nothing.
            }
        };
    }

    protected <T> Collection<? extends Callable<T>> wrap(Context context, Collection<? extends Callable<T>> tasks) {
        List<Callable<T>> wrapped = new ArrayList();
        Iterator iterator = tasks.iterator();
        while(iterator.hasNext()) {
            Callable<T> task = (Callable) iterator.next();
            wrapped.add(wrap(task, context));
        }
        return wrapped;
    }

}
