package com.didiglobal.logi.observability.conponent.thread;

import java.util.concurrent.Callable;

public abstract class CrossThreadCallableWithContextScheduledFuture<T> implements Callable<T> {

    private ContextScheduledFuture contextScheduledFuture;

    public CrossThreadCallableWithContextScheduledFuture(ContextScheduledFuture contextScheduledFuture) {
        this.contextScheduledFuture = contextScheduledFuture;
    }

    public ContextScheduledFuture getContextScheduledFuture() {
        return contextScheduledFuture;
    }

}
