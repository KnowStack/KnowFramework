package com.didiglobal.knowframework.observability.conponent.thread;

import io.opentelemetry.context.Context;

import java.util.concurrent.ScheduledFuture;

public abstract class CrossThreadRunnableWithContextScheduledFuture implements Runnable {

    private ScheduledFuture scheduledFuture;

    public CrossThreadRunnableWithContextScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Context getContext() {
        if(this.scheduledFuture instanceof ContextScheduledFuture) {
            ContextScheduledFuture contextScheduledFuture = (ContextScheduledFuture) this.scheduledFuture;
            return contextScheduledFuture.getContext();
        } else {
            return Context.current();
        }
    }

    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }

}
