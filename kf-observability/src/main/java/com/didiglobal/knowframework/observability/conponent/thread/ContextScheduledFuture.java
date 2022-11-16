package com.didiglobal.knowframework.observability.conponent.thread;

import io.opentelemetry.context.Context;
import java.util.concurrent.*;

public class ContextScheduledFuture<T> implements ScheduledFuture<T> {

    private ScheduledFuture<T> delegate;
    private Context context;

    public ContextScheduledFuture(ScheduledFuture<T> delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return delegate.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        return delegate.compareTo(o);
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
