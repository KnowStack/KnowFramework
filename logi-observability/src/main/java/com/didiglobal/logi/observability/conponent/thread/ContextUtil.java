package com.didiglobal.logi.observability.conponent.thread;

import io.opentelemetry.context.Context;
import java.util.concurrent.Callable;

public class ContextUtil {

    public static <T> Context getContext(Callable<T> task) {
        Context context = null;
        if(task instanceof CrossThreadCallable) {
            CrossThreadCallable crossThreadCallable = (CrossThreadCallable) task;
            context = crossThreadCallable.getContextFuture().getContext();
        } else {
            context = Context.current();
        }
        return context;
    }

    public static Context getContext(Runnable runnable) {
        Context context = null;
        if(runnable instanceof CrossThreadRunnable) {
            CrossThreadRunnable crossThreadRunnable = (CrossThreadRunnable) runnable;
            context = crossThreadRunnable.getContextFuture().getContext();
        } else {
            context = Context.current();
        }
        return context;
    }

}
