package com.didiglobal.logi.observability.conponent.thread;

import io.opentelemetry.context.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

public abstract class CrossThreadCallableWithContextScheduledFuture<T> implements Callable<T> {

    private ScheduledFuture scheduledFuture;

    public CrossThreadCallableWithContextScheduledFuture(ScheduledFuture scheduledFuture) {
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
