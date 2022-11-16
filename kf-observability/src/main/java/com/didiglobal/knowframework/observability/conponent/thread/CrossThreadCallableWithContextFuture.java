package com.didiglobal.knowframework.observability.conponent.thread;

import io.opentelemetry.context.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract class CrossThreadCallableWithContextFuture<T> implements Callable<T> {

    private Future future;

    public CrossThreadCallableWithContextFuture(Future future) {
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
