package com.didiglobal.knowframework.observability.conponent.thread;

import io.opentelemetry.context.Context;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ContextScheduledExecutorService extends ContextExecutorService implements ScheduledExecutorService {

    public ContextScheduledExecutorService(ScheduledExecutorService delegate) {
        super(delegate);
    }

    ScheduledExecutorService delegate() {
        return (ScheduledExecutorService) super.delegate();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        Context context = ContextUtil.getContext(command);
        ScheduledFuture<?> scheduledFuture = this.delegate().schedule(super.wrap(command, context), delay, unit);
        return new ContextScheduledFuture(scheduledFuture, context);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        Context context = ContextUtil.getContext(callable);
        ScheduledFuture<?> scheduledFuture = this.delegate().schedule(super.wrap(callable, context), delay, unit);
        return new ContextScheduledFuture(scheduledFuture, context);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Context context = ContextUtil.getContext(command);
        ScheduledFuture<?> scheduledFuture = this.delegate().scheduleAtFixedRate(super.wrap(command, context), initialDelay, period, unit);
        return new ContextScheduledFuture(scheduledFuture, context);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        Context context = ContextUtil.getContext(command);
        ScheduledFuture<?> scheduledFuture = this.delegate().scheduleWithFixedDelay(super.wrap(command, context), initialDelay, delay, unit);
        return new ContextScheduledFuture(scheduledFuture, context);
    }

}
