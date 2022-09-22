package com.didiglobal.logi.observability.conponent.thread;

public abstract class CrossThreadRunnableWithContextScheduledFuture implements Runnable {

    private ContextScheduledFuture contextScheduledFuture;

    public CrossThreadRunnableWithContextScheduledFuture(ContextScheduledFuture contextScheduledFuture) {
        this.contextScheduledFuture = contextScheduledFuture;
    }

    public ContextScheduledFuture getContextScheduledFuture() {
        return contextScheduledFuture;
    }

}
