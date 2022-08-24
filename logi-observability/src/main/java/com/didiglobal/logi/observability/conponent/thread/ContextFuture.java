package com.didiglobal.logi.observability.conponent.thread;

import io.opentelemetry.context.Context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ContextFuture<T> implements Future<T> {

    private Future<T> delegate;
    private Context context;

    public ContextFuture(Future<T> delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    public Context getContext() {
        return context;
    }

}
