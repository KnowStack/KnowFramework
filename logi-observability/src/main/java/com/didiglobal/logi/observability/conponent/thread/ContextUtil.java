package com.didiglobal.logi.observability.conponent.thread;

import io.opentelemetry.context.Context;
import java.util.concurrent.Callable;

public class ContextUtil {

    public static <T> Context getContext(Callable<T> task) {
        Context context = null;
        if(task instanceof CrossThreadCallableWithContextFuture) {
            CrossThreadCallableWithContextFuture crossThreadCallable = (CrossThreadCallableWithContextFuture) task;
            context = crossThreadCallable.getContextFuture().getContext();
        } else {
            context = Context.current();
        }
        return context;
    }

    public static Context getContext(Runnable runnable) {
        Context context = null;
        if(runnable instanceof CrossThreadRunnableWithContextFuture) {
            CrossThreadRunnableWithContextFuture crossThreadRunnable = (CrossThreadRunnableWithContextFuture) runnable;
            context = crossThreadRunnable.getContextFuture().getContext();
        } else {
            context = Context.current();
        }
        return context;
    }

}
