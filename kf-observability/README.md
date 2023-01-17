# kf-observability

遵从open-telemetry 规范的可观性SDK组件库，融入Grafana生态，为业务应用提供低门槛的Metrics+Log+Trace观测能力。

## 使用简介

在引入observability的maven依赖之后，按需使用提供的组件，即可获得相关组件使用时产生的符合OpenTelemetry规范的上下文信息，与异常监控信息，最终在日志中输出。用户可以通过第三方组件对日志进行上报分析或可视化。也可以通过我们提供的kf-log中的组件上报到ES，通过引入我们提供的grafana模版在Grafana大盘中进行相关信息的查看与指标的监测。

### Maven依赖
```xml
    <dependency>
    	<groupId>io.github.knowstack</groupId>
    	<artifactId>kf-observability</artifactId>
    	<version>1.0.2</version>
    </dependency>
```

## 组件使用

### 1. HttpClient组件（HttpUtils）

根据需要，调用HttpUtils类对应方法，即可自动在Http头注入对应符合OpenTelemetry规范的上下文信息，如：
```java
    String url = "http://localhost:9010/v1/kf-job/task";
    Map<String, Object> params = new HashMap<>();
    params.put("name", "带参数的定时任务");
    params.put("description", "带参数的定时任务");
    params.put("cron", "0 0/1 * * * ? *");
    params.put("className", "com.didichuxing.datachannel.agentmanager.task.JobBroadcasWithParamtTest");
    params.put("params", "{\"name\":\"william\", \"age\":30}");
    params.put("consensual", "RANDOM");
    params.put("nodeNameWhiteListString", "[\"node1\"]");
    String content = JSON.toJSONString(params);
    String response = HttpUtils.postForString(url, content, null);
    System.err.println(response);
```
### 2. Servlet Filter 组件（ObservabilityFilter）

作为Http网关拦截入口，用于解析 & 注入Http头部符合OpenTelemetry规范的上下文信息，构建对应span，并对请求处理过程是否出现异常与非200状态码进行监控。

#### 2.1集成方式

需要将 ObservabilityFilter 类处于 spring 可扫描范围，如：
```java
    @EnableAsync
    @EnableScheduling
    @ServletComponentScan
    @SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.knowframework"})
    public class AgentManagerApplication {
    	
    }
```
**需要注意的是，Filter 仅对 Http 响应状态码为非 200，进行错误标注，对于应用内置状态码不做对应校验控制。**

### 3. Spring AOP 组件（LogAdvice）

作为对Spring应用内部方法调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对方法处理过程是否出现异常进行监控。

#### 3.1集成方式

需要将 ConfigurableAdvisorConfig 类处于 spring 可扫描范围，如：
```java
    @EnableAsync
    @EnableScheduling
    @ServletComponentScan
    @SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.knowframework"})
    public class AgentManagerApplication {
    	
    }
```

### 4. Mybatis Interceptor 组件（ObservabilityInterceptor）

作为对Mybatis内部SQL调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对SQL调用过程是否出现异常进行监控。

#### 4.1 集成方式

4.1.1 纯 Mabatis 应用

将 ObservabilityInterceptor 添加进 SqlSessionFactory Configuration 中的 interceptorChain 中，如：
```java
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(dataSource);
    bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
    SqlSessionFactory sqlSessionFactory = bean.getObject(); // 设置mybatis的xml所在位置
    sqlSessionFactory.getConfiguration().addInterceptor(new ObservabilityInterceptor());
    return sqlSessionFactory;
```

4.1.2 Spring + MyBatis

需要将 ApplicationMybatisInjectionListener 类处于 spring 可扫描范围，如：
```java
    @EnableAsync
    @EnableScheduling
    @ServletComponentScan
    @SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.knowframework"})
    public class AgentManagerApplication {
    	
    }
```
### 5. Thread 组件

作为线程池创建入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对线程运行方法处理过程是否出现异常进行监控。支持两种类型的线程池接口：

1.  ExecutorService
2.  ScheduledExecutorService

支持基于返回值 Future 的多个不同线程的上下文串联。

#### 5.1 非跨线程池的上下文串联

5.1.1 ExecutorService 使用示例：

```java
package com.didiglobal.logi.observability.thread;


import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import io.opentelemetry.api.trace.Tracer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.*;


public class ExecutorServiceNonCrossThreadTest {


    private static Tracer tracer = Observability.getTracer(ExecutorServiceNonCrossThreadTest.class.getName());
    private static final ILog logger = LogFactory.getLog(ExecutorServiceNonCrossThreadTest.class);


    public static void main(String[] args) throws InterruptedException {
        /*
         * 1.封装线程池
         */
        ExecutorService threadPool1 = Observability.wrap(
                new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>( 100 ),
                        new BasicThreadFactory.Builder().namingPattern("main-1").build())
        );
        ExecutorService threadPool2 = Observability.wrap(
                new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>( 100 ),
                        new BasicThreadFactory.Builder().namingPattern("main-2").build())
        );
        logger.info("start function main()");
        /*
         * 2.提交附带返回值任务
         */
        Future<String> future = threadPool1.submit(new MyCallable());
        /*
         * 3.将返回值作为入参，交付新线程进行执行
         */
        threadPool2.submit(new MyRunnable(future));


        Thread.sleep(1000 * 60 * 4);
    }


    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            logger.info("MyCallable.call()");
            return "SUCCESSFUL";
        }
    }


    static class MyRunnable implements Runnable {
        private Future future;
        public MyRunnable(Future future) {
            this.future = future;
        }
        @SneakyThrows
        @Override
        public void run() {
            try {
                String msg = future.get().toString();
                logger.info("MyRunnable.run()");
                logger.info(" parameter is : " + msg);
            } catch (Exception ex) {


            }
        }
 
```

3.5.1.2 ScheduledExecutorService 使用示例：
```java

    package com.didiglobal.logi.observability.thread;


    import com.didiglobal.logi.log.ILog;
    import com.didiglobal.logi.log.LogFactory;
    import com.didiglobal.logi.observability.Observability;
    import io.opentelemetry.api.trace.Tracer;
    import lombok.SneakyThrows;


    import java.util.concurrent.*;


    public class ScheduledExecutorServiceNonCrossThreadTest {


        private static Tracer tracer = Observability.getTracer(ScheduledExecutorServiceNonCrossThreadTest.class.getName());
        private static final ILog logger = LogFactory.getLog(ScheduledExecutorServiceNonCrossThreadTest.class);


        public static void main(String[] args) throws InterruptedException {


            /*
             * 1.封装线程池
             */
            ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
            ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));
            logger.info("start function main()");
            /*
             * 2.提交附带返回值任务
             */
            ScheduledFuture<String> scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
            /*
             * 3.将返回值作为入参，交付新线程进行执行
             */
            threadPool2.scheduleWithFixedDelay(new MyRunnable(scheduledFuture),10, 5 * 1000 * 60, TimeUnit.SECONDS);


            Thread.sleep(1000 * 60 * 4);
        }


        static class MyCallable implements Callable<String> {
            @Override
            public String call() throws Exception {
                logger.info("MyCallable.call()");
                return "SUCCESSFUL";
            }
        }


        static class MyRunnable implements Runnable {
            private ScheduledFuture scheduledFuture;
            public MyRunnable(ScheduledFuture scheduledFuture) {
                this.scheduledFuture = scheduledFuture;
            }
            @SneakyThrows
            @Override
            public void run() {
                try {
                    String msg = scheduledFuture.get().toString();
                    logger.info("MyRunnable.run()");
                    logger.info(" parameter is : " + msg);
                } catch (Exception ex) {


                }
            }
```
#### 3.5.2 跨线程池的上下文串联

3.5.2.1 ExecutorService 使用示例：

```java
package com.didiglobal.logi.observability.thread;


import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.CrossThreadRunnableWithContextFuture;
import io.opentelemetry.api.trace.Tracer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.*;


public class ExecutorServiceCrossThreadTest {


    private static Tracer tracer = Observability.getTracer(ExecutorServiceCrossThreadTest.class.getName());
    private static final ILog logger = LogFactory.getLog(ExecutorServiceCrossThreadTest.class);


    public static void main(String[] args) throws InterruptedException {
        /*
         * 1.封装线程池
         */
        ExecutorService threadPool1 = Observability.wrap(
                new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>( 100 ),
                        new BasicThreadFactory.Builder().namingPattern("main-1").build())
        );
        ExecutorService threadPool2 = Observability.wrap(
                new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>( 100 ),
                        new BasicThreadFactory.Builder().namingPattern("main-2").build())
        );
        logger.info("start function main()");
        /*
         * 2.提交附带返回值任务
         */
        Future<String> future = threadPool1.submit(new MyCallable());
        /*
         * 3.将返回值作为入参，交付新线程进行执行
         */
        threadPool2.submit(new MyRunnable(future));


        Thread.sleep(1000 * 60 * 4);
    }


    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            logger.info("MyCallable.call()");
            return "SUCCESSFUL";
        }
    }


    static class MyRunnable extends CrossThreadRunnableWithContextFuture {


        public MyRunnable(Future future) {
            super(future);
        }


        @SneakyThrows
        @Override
        public void run() {
            logger.info("MyRunnable.run()");
            logger.info(" parameter is : " + super.getFuture().get().toString());
        }

```

3.5.2.2 ScheduledExecutorService 使用示例：

```java
package com.didiglobal.logi.observability.thread;


import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.CrossThreadRunnableWithContextScheduledFuture;
import io.opentelemetry.api.trace.Tracer;
import lombok.SneakyThrows;


import java.util.concurrent.*;


public class ScheduledExecutorServiceCrossThreadTest {


    private static Tracer tracer = Observability.getTracer(ScheduledExecutorServiceCrossThreadTest.class.getName());
    private static final ILog logger = LogFactory.getLog(ScheduledExecutorServiceCrossThreadTest.class);


    public static void main(String[] args) throws InterruptedException {


        /*
         * 1.封装线程池
         */
        ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledFuture<String> scheduledFuture = null;
        logger.info("start function main()");
        /*
         * 2.提交附带返回值任务
         */
        scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
        /*
         * 3.将返回值作为入参，交付新线程进行执行
         */
        threadPool2.scheduleWithFixedDelay(new MyRunnable(scheduledFuture),10, 10, TimeUnit.SECONDS);


        Thread.sleep(1000 * 60 * 4);
    }


    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            logger.info("MyCallable.call()");
            return "SUCCESSFUL";
        }
    }


    static class MyRunnable extends CrossThreadRunnableWithContextScheduledFuture {


        public MyRunnable(ScheduledFuture scheduledFuture) {
            super(scheduledFuture);
        }


        @SneakyThrows
        @Override
        public void run() {
            logger.info("MyRunnable.run()");
            logger.info(" parameter is : " + super.getScheduledFuture().get().toString());
        
```

# 观测工具搭建

在我们内部研发与接入过程中，我们整理了一套由`knowframework`+`ES`+`Grafana`协同的可观测工具的搭建思路，`kf-observability`负责trace、metric的处理与收集，`kf-log`负责数据上报、ES负责数据存储，最后由`Grafana`负责数据展现。

## 默认工具体系搭建

1.  添加`kf-log`与`kf-observability`的maven依赖；
2.  按需配置并使用`kf-observability`中提供的组件收集数据；
3.  将应用中的日志对象替换为`kf-log`提供的`ILog`的实现，如：
``` java
    ILog bootLogger = LogFactory.getLog(this.getClass);
```
4.  配置log4j2的xml配置，使用`ElasticsearchAppender`进行日志的输出与上报。（kf-log的详细配置与使用见：[kf-log](../kf-log)）

样例如下：
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration package="org.apache.logging.log4j.core,com.didiglobal.knowframework.log.log4j2.appender"
                   status="error" monitorInterval="60">
        <Appenders>
            <Appenders>
                <console name="console" target="SYSTEM_OUT"/>
                <ElasticsearchAppender name="esAppender" address="localhost" port="9200" user="admin" password="admin"
                                       indexName="index_observability" typeName="type" threshold="all" bufferSize="1000"
                                       numberOfShards="1" numberOfReplicas="1">
                </ElasticsearchAppender>
            </Appenders>


        </Appenders>
        <Loggers>
            <root>
                <!-- 日志上报ES -->
                <AppenderRef ref="esAppender"/>
                <!-- 控制台输出 -->
                <AppenderRef ref="console"/>
            </root>
        </Loggers>
    </Configuration>
```

5.  安装配置Grafana，详情参见：[Grafana_UserManual.md](./user_manual/Grafana_UserManual.md)

## 自行搭建其他观测工具

### 1. 自定义数据输出与数据展现

`kf-observability`提供遵从`open-telemetry`规范的数据，用户可根据需求自行实现`MetricExporter`与`SpanExporter`进行数据的输出与上报。并对接自己的可观测体系。

### 2. 使用我们提供的数据格式，自定义数据上报与数据展现

`kf-observability`提供遵从`open-telemetry`规范的数据，内部默认的exporter实现会将数据写入日志中，具体样例如下：

#### metrics
```json
    {
        "applicationName": "arius-admin",
        "data": {
            "attributes":{
           {"key":"diskPath","keyUtf8":"ZGlza1BhdGg=","type":"STRING"}:"/"
            },
            "exemplarDataList": [],
            "instrumentationScopeInfoName": "metrics.platform",
            "metricDescription": "磁盘分区余量大小（单位：Byte）",
            "metricEpochNanos": 1673594868514000000,
            "metricName": "system.disk.free",
            "metricType": "DOUBLE_GAUGE",
            "metricUnit": "Bytes",
            "metricValue": 107374182400
        },
        "hostName": "didideMacBook-Pro.local",
        "ip": "172.28.158.149",
        "logEventType": "METRIC"
    }
```
#### span（trace）
```json
    {
        "applicationName": "arius-admin",
        "data": {
            "attributes": {},
            "endEpochNanos": 1673594841053330018,
            "parentSpanId": "2f814063388cc7d8",
            "spanId": "6d4b7d39566d5698",
            "spanKind": "INTERNAL",
            "spanName": "com.didichuxing.datachannel.arius.admin.core.service.es.impl.ESClusterNodeServiceImpl.syncGetPlugins",
            "spentNanos": 202952643,
            "startEpochNanos": 1673594840850377375,
            "statusData": "OK",
            "traceId": "a7cfe4f0bef778370771d7d8ae8cfbc7",
            "tracerName": "com.didiglobal.knowframework.observability.conponent.spring.aop.LogAdvice"
        },
        "hostName": "didideMacBook-Pro.local",
        "ip": "172.28.158.149",
        "logEventType": "TRACE"
    }
```

### 3. 使用我们提供的数据格式，与数据上报组件，自定义数据展现，对接可观测体系

`kf-observability`提供遵从`open-telemetry`规范的数据，内部默认的exporter实现会将数据写入日志中，并且根据log4j2中elasticsearchAppder的配置上报到ES中，用户对接前需要按照 **[默认工具体系搭建]** 中的1、2、3、4进行接入与配置，并自行选择读取es数据的前端展现组件；

#### ES中的索引Mapping如下：
```json
    {
        "properties": {
            "JobClassName": {
                "type": "keyword"
            },
            "TaskName": {
                "type": "keyword"
            },
            "applicationName": {
                "type": "keyword"
            },
            "className": {
                "type": "keyword"
            },
            "component": {
                "type": "keyword"
            },
            "deviceName": {
                "type": "keyword"
            },
            "diskPath": {
                "type": "keyword"
            },
            "endEpochNanos": {
                "type": "long"
            },
            "exemplarData": {
                "type": "keyword"
            },
            "fileName": {
                "type": "keyword"
            },
            "hostName": {
                "type": "keyword"
            },
            "httpHost": {
                "type": "keyword"
            },
            "httpMethod": {
                "type": "keyword"
            },
            "httpScheme": {
                "type": "keyword"
            },
            "httpTarget": {
                "type": "keyword"
            },
            "httpUrl": {
                "type": "keyword"
            },
            "instrumentationScopeInfoName": {
                "type": "keyword"
            },
            "ip": {
                "type": "keyword"
            },
            "lineNumber": {
                "type": "long"
            },
            "logLevel": {
                "type": "keyword"
            },
            "logMills": {
                "type": "date"
            },
            "logName": {
                "type": "keyword"
            },
            "logThread": {
                "type": "keyword"
            },
            "logType": {
                "type": "keyword"
            },
            "message": {
                "type": "text",
                "copy_to": [
                    "message_full_name"
                ]
            },
            "message_full_name": {
                "type": "keyword"
            },
            "methodName": {
                "type": "keyword"
            },
            "metricDescription": {
                "type": "keyword"
            },
            "metricEpochNanos": {
                "type": "date"
            },
            "metricName": {
                "type": "keyword"
            },
            "metricType": {
                "type": "keyword"
            },
            "metricUnit": {
                "type": "keyword"
            },
            "metricValue": {
                "type": "double"
            },
            "parentSpanId": {
                "type": "keyword"
            },
            "spanId": {
                "type": "keyword"
            },
            "spanKind": {
                "type": "keyword"
            },
            "spanName": {
                "type": "keyword"
            },
            "spentNanos": {
                "type": "long"
            },
            "sqlStatement": {
                "type": "keyword"
            },
            "sqlType": {
                "type": "keyword"
            },
            "startEpochNanos": {
                "type": "long"
            },
            "statusData": {
                "type": "keyword"
            },
            "traceId": {
                "type": "keyword"
            },
            "tracerName": {
                "type": "keyword"
            },
            "tracerVersion": {
                "type": "keyword"
            }
        }
    }
```
#### ES中数据格式如下：

**metric**
```json
    {
        "logType": "METRIC",
        "hostName": "shizeyingdeMBP.lan",
        "fileName": "LoggingMetricExporter.java",
        "metricName": "process.jvm.eu",
        "logThread": "PeriodicMetricReader-1",
        "ip": "172.28.37.177",
        "metricValue": 0,
        "methodName": "export",
        "className": "com.didiglobal.knowframework.observability.exporter.LoggingMetricExporter",
        "metricEpochNanos": 1673858437747,
        "metricUnit": "Bytes",
        "metricType": "DOUBLE_GAUGE",
        "metricDescription": "JVM进程 Eden 区使用量大小（单位：Byte）",
        "logName": "com.didiglobal.knowframework.observability.exporter.LoggingSpanExporter",
        "logLevel": "INFO",
        "logMills": 1673858437768,
        "instrumentationScopeInfoName": "metrics.platform",
        "lineNumber": 55,
        "applicationName": "arius-admin"
    }
```

**span（trace）**
```json
    {
        "logType": "TRACE",
        "traceId": "cd31b286a21e32bda4dbbcf44df85546",
        "hostName": "didideMacBook-Pro.local",
        "fileName": "LoggingSpanExporter.java",
        "endEpochNanos": 1673855649056740600,
        "spanKind": "INTERNAL",
        "logThread": "BatchSpanProcessor_WorkerThread-1",
        "ip": "172.28.138.211",
        "methodName": "export",
        "className": "com.didiglobal.knowframework.observability.exporter.LoggingSpanExporter",
        "spentNanos": 113048033,
        "parentSpanId": "d40deea71a7c37bb",
        "spanName": "com.didiglobal.knowframework.job.mapper.LogIJobMapper.insert",
        "spanId": "0a02391d69b38257",
        "startEpochNanos": 1673855648943692500,
        "logName": "com.didiglobal.knowframework.observability.exporter.LoggingSpanExporter",
        "logLevel": "INFO",
        "logMills": 1673855654042,
        "tracerName": "com.didiglobal.knowframework.observability.conponent.spring.aop.LogAdvice",
        "lineNumber": 60,
        "statusData": "OK",
        "applicationName": "arius-admin"
    }
```


# 高级用法

用于更改 Observability 组件默认行为，需要在 classpath 路径下新建文件 observability.properties，其格式为普通的 properties 文件，示例：
```
    application.name=logi-job-test
    metric.export.interval.ms=60000
    observability.initializer.classes=com.didiglobal.logi.job.examples.MyMetricinitializer
    pointcut=execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..))
    # 配置当前启用的exporter：LoggingMetricExporter,LoggingSpanExporter
    observability.exporter.names=LoggingMetricExporter,LoggingSpanExporter
```
## 1. Spring AOP 串联自定义包下的类的方法

修改配置文件 observability.properties 中配置项`pointcut`的值，如自定义包名为 com.custom，则需要将配置项`pointcut`的值修改为：`execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..)) || execution(* com.custom..*.*(..))`。

需要注意的是，必须保留`execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..))`，自定义包加在这个配置项值后面。

## 2. 上报自定义指标

创建一个用于注册自定义上报指标的类，该类需要继承类 BaseMetricInitializer，并重写 register 方法进行自定义指标注册，使用示例：

```java
package com.didiglobal.knowframework.job.examples;


import com.didiglobal.knowframework.observability.conponent.metrics.BaseMetricInitializer;
import com.didiglobal.knowframework.observability.conponent.metrics.Meter;
import com.didiglobal.knowframework.observability.conponent.metrics.Metric;
import com.didiglobal.knowframework.observability.conponent.metrics.MetricUnit;
import java.util.HashMap;
import java.util.Map;


public class MyMetricInitializer extends BaseMetricInitializer {


    @Override
    public void register() {
        super.registerMetric(
                "docs",
                "number of docs",
                MetricUnit.METRIC_UNIT_NUMBER,
                new Meter() {
                    @Override
                    public Metric getMetric() {
                        Map<String, String> tags = new HashMap<>();
                        tags.put("docType", "humanities");
                        return new Metric(1.0, tags);
                    }
                }
        );
    }

```

修改配置文件`observability.properties`中配置项`observability.initializer.classes`的值，将用于注册自定义上报指标的类的完整名（含：包名）作为该配置项的值填入，使用示例：

`observability.initializer.classes=com.didiglobal.logi.job.examples.MyMetricinitializer`

需要注意的是该配置项支持配置多个类，多个类之间采用逗号分隔。

## 3. 调整指标上报周期

修改配置文件`observability.properties`中配置项`metric.export.interval.ms`的值（单位：毫秒），该配置项默认值为 60000（即：1分钟）。

## 4. 调整Metric/Log/Trace上报时的应用名

修改配置文件`observability.properties`中配置项`application.name`的值，该配置项默认值为 application.default。

## 5. 调整Metric/Trace 是否上报

修改配置文件`observability.properties`中配置项`observability.exporter.names`的值，该配置项默认值为空，可选`LoggingMetricExporter`、`LoggingSpanExporter`多个用逗号隔开。
