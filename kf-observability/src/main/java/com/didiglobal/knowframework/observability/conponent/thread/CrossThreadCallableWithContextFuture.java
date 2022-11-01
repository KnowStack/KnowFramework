package com.didiglobal.knowframework.observability.conponent.thread;

import java.util.concurrent.Callable;

public abstract class CrossThreadCallableWithContextFuture<T> implements Callable<T> {

    private ContextFuture contextFuture;

    public CrossThreadCallableWithContextFuture(ContextFuture contextFuture) {
        this.contextFuture = contextFuture;
    }

    public ContextFuture getContextFuture() {
        return contextFuture;
    }

}
