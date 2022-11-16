package com.didiglobal.logi.observability.conponent.thread;

import io.opentelemetry.context.Context;

import java.util.concurrent.Future;

public abstract class CrossThreadRunnableWithContextFuture implements Runnable {

    private Future future;

    public CrossThreadRunnableWithContextFuture(Future future) {
        this.future = future;
    }

    public Context getContext() {
        if(this.future instanceof ContextFuture) {
            ContextFuture contextFuture = (ContextFuture) this.future;
            return contextFuture.getContext();
        } else {
            return Context.current();
        }
    }

    public Future getFuture() {
        return future;
    }

}
