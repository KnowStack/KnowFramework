package com.didiglobal.logi.observability.conponent.thread;

import java.util.concurrent.Callable;

public abstract class CrossThreadCallable<T> implements Callable<T> {

    private ContextFuture contextFuture;

    public CrossThreadCallable(ContextFuture contextFuture) {
        this.contextFuture = contextFuture;
    }

    public ContextFuture getContextFuture() {
        return contextFuture;
    }

}
