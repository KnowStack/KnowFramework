package com.didiglobal.knowframework.observability.conponent.thread;

import io.opentelemetry.context.Context;

import java.util.concurrent.ScheduledFuture;

public abstract class CrossThreadRunnableWithContextScheduledFuture<T> implements Runnable {

    private ScheduledFuture<T> scheduledFuture;

    protected CrossThreadRunnableWithContextScheduledFuture(ScheduledFuture<T> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Context getContext() {
        if(this.scheduledFuture instanceof ContextScheduledFuture) {
            ContextScheduledFuture<T> contextScheduledFuture = (ContextScheduledFuture) this.scheduledFuture;
            return contextScheduledFuture.getContext();
        } else {
            return Context.current();
        }
    }

    public ScheduledFuture<T> getScheduledFuture() {
        return scheduledFuture;
    }

}
