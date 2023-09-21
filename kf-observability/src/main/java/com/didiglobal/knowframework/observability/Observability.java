package com.didiglobal.knowframework.observability;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.didiglobal.knowframework.observability.common.DefaultOpenTelemetryExpandFactory;
import com.didiglobal.knowframework.observability.common.OpenTelemetryExpandFactory;
import com.didiglobal.knowframework.observability.common.util.PropertiesUtil;
import com.didiglobal.knowframework.observability.conponent.metrics.PlatformMetricsInitializer;
import com.didiglobal.knowframework.observability.conponent.thread.ContextExecutorService;
import com.didiglobal.knowframework.observability.conponent.thread.ContextScheduledExecutorService;
import com.didiglobal.knowframework.observability.exporter.LoggingMetricExporter;
import com.didiglobal.knowframework.observability.exporter.LoggingSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Observability {

    private static final Logger LOGGER = LoggerFactory.getLogger(Observability.class);

    /**
     * OpenTelemetry 对象
     */
    private static OpenTelemetry delegate;
    /**
     * 指标数据输出间隔时间 默认值：1 s
     */
    private static Long metricExportIntervalMs;
    /**
     * 应用名 默认值：""
     */
    private static String applicationName;
    /**
     * 初始化器类（集）默认值：""
     */
    private static String observabilityInitializerClasses;
    /**
     * openTelemetry拓展工厂
     */
    private static String openTelemetryExpandSpanFactoryClass;
    /**
     * 已开启的exporter
     */
    private static Set<String> exporterNameSet;
    private static final Set<SpanProcessor> expandSpanProcessorSet = new HashSet<>();
    private static final String PROPERTIES_KEY_APPLICATION_NAME = "application.name";
    private static final String PROPERTIES_KEY_METRIC_EXPORT_INTERVAL_MS = "metric.export.interval.ms";
    private static final String PROPERTIES_KEY_OBSERVABILITY_INITIALIZER_CLASSES = "observability.initializer.classes";
    private static final String OPEN_TELEMETRY_EXPAND_FACTORY_CLASS = "observability.open.telemetry.expand.factory";

    /**
     * 配置开启哪些支持的exporter
     */
    private static final String PROPERTIES_KEY_OBSERVABILITY_EXPORTERS_SWITCH = "observability.exporter.names";
    private static final String APPLICATION_NAME_DEFAULT_VALUE = "application.default";
    private static final Long METRIC_EXPORT_INTERVAL_MS_DEFAULT_VALUE = 1000 * 60L;
    private static final String OBSERVABILITY_INITIALIZER_CLASSES_DEFAULT_VALUE = "";

    static {
        initParams();
        delegate = initOpenTelemetry();
        init();
    }

    private static void init() {
        List<ObservabilityInitializer> observabilityInitializerList = new ArrayList<>();
        observabilityInitializerList.add(new PlatformMetricsInitializer());
        /*
         * 加载外部初始化器
         */
        try {
            if (StringUtils.isNotBlank(observabilityInitializerClasses)) {
                String[] observabilityInitializerClassArray = observabilityInitializerClasses.split(",");
                for (String observabilityInitializerClass : observabilityInitializerClassArray) {
                    observabilityInitializerList.add(
                            (ObservabilityInitializer) Class.forName(observabilityInitializerClass).newInstance()
                    );
                }
            }
        } catch (Exception ex) {
            LOGGER.error(
                    String.format(
                            "class=Observability||method=init||message=加载配置的初始化器%s失败",
                            observabilityInitializerClasses
                    )
            );
        }
        /*
         * 执行初始化动作
         */
        for (ObservabilityInitializer observabilityInitializer : observabilityInitializerList) {
            try {
                observabilityInitializer.startup();
            } catch (Exception ex) {
                LOGGER.error(
                        String.format(
                                "class=Observability||method=init||message=初始化器%s执行失败",
                                observabilityInitializer.getClass().getName()
                        )
                );
            }
        }
    }

    private static void initParams() {
        Properties properties = PropertiesUtil.getProperties();
        /*
         * load applicationName
         */
        applicationName = properties.getProperty(PROPERTIES_KEY_APPLICATION_NAME);
        if (StringUtils.isBlank(applicationName)) {
            applicationName = APPLICATION_NAME_DEFAULT_VALUE;
        }
        /*
         * load metricExportIntervalMs
         */
        String metricExportIntervalMsStr = properties.getProperty(PROPERTIES_KEY_METRIC_EXPORT_INTERVAL_MS);
        if (StringUtils.isBlank(metricExportIntervalMsStr)) {
            metricExportIntervalMs = METRIC_EXPORT_INTERVAL_MS_DEFAULT_VALUE;
        } else {
            try {
                metricExportIntervalMs = Long.valueOf(metricExportIntervalMsStr);
            } catch (Exception ex) {
                metricExportIntervalMs = METRIC_EXPORT_INTERVAL_MS_DEFAULT_VALUE;
            }
        }
        /*
         * load observabilityInitializerClasses
         */
        observabilityInitializerClasses = properties.getProperty(PROPERTIES_KEY_OBSERVABILITY_INITIALIZER_CLASSES);
        if (StringUtils.isBlank(observabilityInitializerClasses)) {
            observabilityInitializerClasses = OBSERVABILITY_INITIALIZER_CLASSES_DEFAULT_VALUE;
        }
        /*
         * load open exporters
         */
        exporterNameSet = new ConcurrentHashSet<>();
        String exporters = properties.getProperty(PROPERTIES_KEY_OBSERVABILITY_EXPORTERS_SWITCH);
        if (StringUtils.isNotBlank(exporters)) {
            exporterNameSet.addAll(Arrays.asList(exporters.split(",")));
        }
        /*
         * 加载拓展的spanProcessFactory
         */
        openTelemetryExpandSpanFactoryClass = properties.getProperty(OPEN_TELEMETRY_EXPAND_FACTORY_CLASS);
    }

    /**
     * @return 初始化 OpenTelemetry 对象
     */
    private static OpenTelemetry initOpenTelemetry() {

        // Create an instance of PeriodicMetricReader and configure it
        // to export via the logging exporter
        MetricReader periodicReader =
                PeriodicMetricReader.builder(LoggingMetricExporter.create())
                        .setInterval(Duration.ofMillis(metricExportIntervalMs))
                        .build();
        // This will be used to create instruments
        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder().registerMetricReader(periodicReader).build();
        Resource resource = Resource.getDefault();
        try {
            OpenTelemetryExpandFactory factory;
            if (StringUtils.isNotBlank(openTelemetryExpandSpanFactoryClass)) {
                factory = (OpenTelemetryExpandFactory) Class.forName(openTelemetryExpandSpanFactoryClass).newInstance();
            } else {
                factory = DefaultOpenTelemetryExpandFactory.class.newInstance();
            }
            expandSpanProcessorSet.addAll(factory.getSpanProcessorList());
            resource = factory.getResource();
        } catch (Exception ex) {
            LOGGER.error(
                    String.format(
                            "class=Observability||method=initOpenTelemetry||message=加载配置的OpenTelemetry拓展工厂%s失败",
                            openTelemetryExpandSpanFactoryClass
                    )
            );
        }
        // Tracer provider configured to export spans with SimpleSpanProcessor using
        // the logging exporter.
        SdkTracerProviderBuilder builder = SdkTracerProvider.builder().addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build());
        expandSpanProcessorSet.forEach(builder::addSpanProcessor);
        SdkTracerProvider tracerProvider = builder.setResource(resource).build();
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        Runtime.getRuntime().addShutdownHook(new Thread(meterProvider::close));
        return sdk;
    }

    /**
     * @param instrumentationScopeName 域名
     * @return 获取给定域名对应 meter 对象
     */
    public static Meter getMeter(String instrumentationScopeName) {
        return delegate.getMeter(instrumentationScopeName);
    }

    /**
     * @param instrumentationScopeName 域名
     * @return 获取给定域名对应 tracer 对象
     */
    public static Tracer getTracer(String instrumentationScopeName) {
        return delegate.getTracer(instrumentationScopeName);
    }

    /**
     * @return 获取当前 span id
     */
    public static String getCurrentSpanId() {
        Span span = Span.current();
        if (Span.getInvalid() != span && span.getSpanContext().isValid()) {
            String spanId = span.getSpanContext().getSpanId();
            return spanId;
        } else {
            //当前上下文不存在于任何span中
            return StringUtils.EMPTY;
        }
    }

    /**
     * @return 获取当前 span 所属 trace 对应 trace id
     */
    public static String getCurrentTraceId() {
        Span span = Span.current();
        if (Span.getInvalid() != span && span.getSpanContext().isValid()) {
            String tracerId = span.getSpanContext().getTraceId();
            return tracerId;
        } else {
            //当前上下文不存在于任何span中
            return StringUtils.EMPTY;
        }
    }

    /**
     * @param executor 待封装线程池
     * @return 附带 trace 上下文信息线程池
     */
    public static ExecutorService wrap(ExecutorService executor) {
        return new ContextExecutorService(executor);
    }

    /**
     * @param executor 待封装调度线程池
     * @return 附带 trace 上下文信息调度线程池
     */
    public static ScheduledExecutorService wrap(ScheduledExecutorService executor) {
        return new ContextScheduledExecutorService(executor);
    }

    public static TextMapPropagator getTextMapPropagator() {
        return delegate.getPropagators().getTextMapPropagator();
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static boolean exporterExist(String exporterName) {
        return exporterNameSet.contains(exporterName);
    }

}
