package test;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class Work {

    public static void doWork(Tracer tracer) {
        Span span = tracer.spanBuilder("doWork").setParent(Context.current()).startSpan();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // do the right thing here
        } finally {
            span.end();
        }
    }

    public static void doWork2(Tracer tracer) {
        Span span = tracer.spanBuilder("doWork2").setParent(Context.current()).startSpan();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // do the right thing here
        } finally {
            span.end();
        }
    }

}
