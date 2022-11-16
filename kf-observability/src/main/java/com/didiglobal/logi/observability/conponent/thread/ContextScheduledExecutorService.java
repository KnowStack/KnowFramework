package com.didiglobal.logi.observability.conponent.thread;

import com.didiglobal.logi.observability.Observability;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.util.concurrent.*;

public class ContextScheduledExecutorService extends ContextExecutorService implements ScheduledExecutorService {

    private Tracer tracer = Observability.getTracer(ContextScheduledExecutorService.class.getName());

    public ContextScheduledExecutorService(ScheduledExecutorService delegate) {
        super(delegate);
    }

    ScheduledExecutorService delegate() {
        return (ScheduledExecutorService) super.delegate();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        Context context = ContextUtil.getContext(command);
        if(Context.root().equals(context)) {
            Span span = tracer.spanBuilder(Thread.currentThread().getName()).startSpan();
            try (Scope scope = span.makeCurrent()) {
                context = Context.current();
                ScheduledFuture<?> scheduledFuture = this.delegate().schedule(super.wrap(command, context), delay, unit);
                return new ContextScheduledFuture(scheduledFuture, context);
            } finally {
                span.end();
            }
        } else {
            ScheduledFuture<?> scheduledFuture = this.delegate().schedule(super.wrap(command, context), delay, unit);
            return new ContextScheduledFuture(scheduledFuture, context);
        }
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        Context context = ContextUtil.getContext(callable);
        if(Context.root().equals(context)) {
            Span span = tracer.spanBuilder(Thread.currentThread().getName()).startSpan();
            try (Scope scope = span.makeCurrent()) {
                context = Context.current();
                ScheduledFuture<?> scheduledFuture = this.delegate().schedule(super.wrap(callable, context), delay, unit);
                return new ContextScheduledFuture(scheduledFuture, context);
            } finally {
                span.end();
            }
        } else {
            ScheduledFuture<?> scheduledFuture = this.delegate().schedule(super.wrap(callable, context), delay, unit);
            return new ContextScheduledFuture(scheduledFuture, context);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Context context = ContextUtil.getContext(command);
        if(Context.root().equals(context)) {
            Span span = tracer.spanBuilder(Thread.currentThread().getName()).startSpan();
            try (Scope scope = span.makeCurrent()) {
                context = Context.current();
                ScheduledFuture<?> scheduledFuture = this.delegate().scheduleAtFixedRate(super.wrap(command, context), initialDelay, period, unit);
                return new ContextScheduledFuture(scheduledFuture, context);
            } finally {
                span.end();
            }
        } else {
            ScheduledFuture<?> scheduledFuture = this.delegate().scheduleAtFixedRate(super.wrap(command, context), initialDelay, period, unit);
            return new ContextScheduledFuture(scheduledFuture, context);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        Context context = ContextUtil.getContext(command);
        if(Context.root().equals(context)) {
            Span span = tracer.spanBuilder(Thread.currentThread().getName()).startSpan();
            try (Scope scope = span.makeCurrent()) {
                context = Context.current();
                ScheduledFuture<?> scheduledFuture = this.delegate().scheduleWithFixedDelay(super.wrap(command, context), initialDelay, delay, unit);
                return new ContextScheduledFuture(scheduledFuture, context);
            } finally {
                span.end();
            }
        } else {
            ScheduledFuture<?> scheduledFuture = this.delegate().scheduleWithFixedDelay(super.wrap(command, context), initialDelay, delay, unit);
            return new ContextScheduledFuture(scheduledFuture, context);
        }
    }

}
