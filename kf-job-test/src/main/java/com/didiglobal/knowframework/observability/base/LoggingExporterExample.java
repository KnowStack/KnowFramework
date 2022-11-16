package com.didiglobal.knowframework.observability.base;

import com.didiglobal.knowframework.observability.Observability;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

/**
 * An example of using {@link io.opentelemetry.exporter.logging.LoggingSpanExporter} and {@link
 * io.opentelemetry.exporter.logging.LoggingMetricExporter}.
 */
public final class LoggingExporterExample {
  private static final String INSTRUMENTATION_NAME = LoggingExporterExample.class.getName();

  private final Tracer tracer = Observability.getTracer(INSTRUMENTATION_NAME);

  public void myWonderfulUseCase() {
    Span span = this.tracer.spanBuilder("start my wonderful use case").startSpan();
    try (Scope scope = span.makeCurrent()) {
      span.addEvent("Event 0");
      Work.doWork(tracer);
      span.addEvent("Event 1");
    } finally {
      span.end();
    }
  }

  public static void main(String[] args) {

    // Start the example
    LoggingExporterExample example = new LoggingExporterExample();
    Span span = example.tracer.spanBuilder("main").startSpan();
    try (Scope scope = span.makeCurrent()) {
      // Generate a few sample spans
      for (int i = 0; i < 5; i++) {
        example.myWonderfulUseCase();
      }
    } finally {
      span.end();
    }

    Span span2 = example.tracer.spanBuilder("main2").startSpan();
    try (Scope scope = span2.makeCurrent()) {
      Work.doWork2(example.tracer);
    } finally {
      span2.end();
    }

    try {
      // Flush out the metrics that have not yet been exported
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      // ignore since we're exiting
    }
    System.out.println("Bye");

  }
}
