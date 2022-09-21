package com.didiglobal.logi.observability.conponent.thread;

public abstract class CrossThreadRunnable implements Runnable {

    private ContextFuture contextFuture;

    public CrossThreadRunnable(ContextFuture contextFuture) {
        this.contextFuture = contextFuture;
    }

    public ContextFuture getContextFuture() {
        return contextFuture;
    }

}
