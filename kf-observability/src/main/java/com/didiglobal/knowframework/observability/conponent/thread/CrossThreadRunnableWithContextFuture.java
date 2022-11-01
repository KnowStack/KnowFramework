package com.didiglobal.knowframework.observability.conponent.thread;

public abstract class CrossThreadRunnableWithContextFuture implements Runnable {

    private ContextFuture contextFuture;

    public CrossThreadRunnableWithContextFuture(ContextFuture contextFuture) {
        this.contextFuture = contextFuture;
    }

    public ContextFuture getContextFuture() {
        return contextFuture;
    }

}
