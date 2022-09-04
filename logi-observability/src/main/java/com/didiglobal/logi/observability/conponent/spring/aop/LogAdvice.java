package com.didiglobal.logi.observability.conponent.spring.aop;

import com.didiglobal.logi.observability.Observability;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LogAdvice implements MethodInterceptor {

    private Tracer tracer = Observability.getTracer(LogAdvice.class.getName());

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String clazzName = methodInvocation.getThis().getClass().getName();
        String methodName = methodInvocation.getMethod().getName();
        Span span = tracer.spanBuilder(String.format("%s.%s", clazzName, methodName)).startSpan();
        try (Scope scope = span.makeCurrent()) {
            Object result = methodInvocation.proceed();
            span.setStatus(StatusCode.OK);
            return result;
        } catch (Throwable ex) {
            span.setStatus(StatusCode.ERROR, ex.getMessage());
            throw ex;
        } finally {
            span.end();
        }
    }
}
