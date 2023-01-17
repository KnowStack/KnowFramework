# 基础用法

## 1. 添加maven依赖

```xml
<dependency>
	<groupId>io.github.zqrferrari</groupId>
	<artifactId>logi-observability</artifactId>
	<version>1.0.2</version>
</dependency>
```

## 2. 组件使用

### 2.1 HttpClient组件

根据需要，调用HttpUtils类对应方法，即可自动在Http头注入对应符合OpenTelemetry规范的上下文信息，示例：

```java
String url = "http://localhost:9010/v1/logi-job/task";
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

### 3.2 Servlet Filter 组件

作为Http网关拦截入口，用于解析 & 注入Http头部符合OpenTelemetry规范的上下文信息，构建对应span，并对请求处理过程是否出现异常与非200状态码进行监控。

#### 3.2.1集成方式

需要将 com.didiglobal.logi.observability.conponent.spring.filter.ObservabilityFilter 类处于 spring 可扫描范围，示例：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {
	
}
```

**需要注意的是，Filter 仅对 Http 响应状态码为非 200，进行错误标注，对于应用内置状态码不做对应校验控制**。

### 3.3 Spring AOP 组件

作为对Spring应用内部方法调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对方法处理过程是否出现异常进行监控。

#### 3.3.1集成方式

需要将 com.didiglobal.logi.observability.conponent.spring.aop.ConfigurableAdvisorConfig 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {
	
}
```

**需要注意的是，默认构建span的方法位于 com.didiglobal 与 com.didichuxing 包下的类中，如需要构建span的方法不在这两个包下的类中，需要修改 observability.properties 文件中的配置项 pointcut 值，详见高级用法部分**。

### 3.4 Mybatis Interceptor 组件

作为对Mybatis内部SQL调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对SQL调用过程是否出现异常进行监控。

#### 3.4.1集成方式

##### 3.4.1.1 纯 Mabatis 应用

将 com.didiglobal.logi.observability.conponent.mybatis.ObservabilityInterceptor 添加进 SqlSessionFactory Configuration 中的 interceptorChain 中，如：

```java
SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
bean.setDataSource(dataSource);
bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
SqlSessionFactory sqlSessionFactory = bean.getObject(); // 设置mybatis的xml所在位置
sqlSessionFactory.getConfiguration().addInterceptor(new ObservabilityInterceptor());
return sqlSessionFactory;
```

##### 3.4.1.2 Spring + MyBatis 应用

需要将 com.didiglobal.logi.observability.conponent.mybatis.ApplicationMybatisInjectionListener 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {
	
}
```

**注意：如项目未用到Mybatis，需要在注解 @SpringBootApplication 中添加如下属性 (exclude={DataSourceAutoConfiguration.class})，添加后如下：@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})。**

### 3.5 Thread 组件

作为线程池创建入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对线程运行方法处理过程是否出现异常进行监控。支持两种类型的线程池接口：

1. ExecutorService
2. ScheduledExecutorService

#### 3.5.1 非跨线程池的上下文串联

##### 3.5.1.1 ExecutorService 使用示例：

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
    }

}
```

##### 3.5.1.2 ScheduledExecutorService 使用示例：

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
    }

}
```

#### 3.5.2 跨线程池的上下文串联

##### 3.5.2.1 ExecutorService 使用示例：

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
    }

}
```

##### 3.5.2.2 ScheduledExecutorService 使用示例：

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
        }

    }

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

修改配置文件 observability.properties 中配置项 `pointcut` 的值，如自定义包名为 com.custom，则需要将配置项 `pointcut` 的值修改为：`execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..)) || execution(* com.custom..*.*(..))`。

**需要注意的是，必须保留`execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..))`，自定义包加在这个配置项值后面。**

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

}
```

修改配置文件 observability.properties 中配置项 `observability.initializer.classes` 的值，将用于注册自定义上报指标的类的完整名（含：包名）作为该配置项的值填入，使用示例：

`observability.initializer.classes=com.didiglobal.logi.job.examples.MyMetricinitializer`

**需要注意的是该配置项支持配置多个类，多个类之间采用逗号分隔。**

## 3. 调整指标上报周期

修改配置文件 observability.properties 中配置项 `metric.export.interval.ms` 的值（单位：毫秒），该配置项默认值为 60000（即：1分钟）。

## 4. 调整Metric/Log/Trace上报时的应用名

修改配置文件 observability.properties 中配置项 `application.name` 的值，该配置项默认值为 application.default。

## 5. 调整Metric/Trace 是否上报

修改配置文件 observability.properties 中配置项 `observability.exporter.names` 的值，该配置项默认值为空，可选 `LoggingMetricExporter`、`LoggingSpanExporter`多个用逗号隔开。