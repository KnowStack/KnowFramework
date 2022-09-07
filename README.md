# 项目概述
LogiCommon 公共模块，包括Logi-security、Logi-job 。LogiCommon 会尽量保持与具体业务的解耦，以简单易用，轻量高效，尽可能减少用户感知的方式做到多平台适配，同时提供可扩展的接口，让用户自由的扩展 LogiCommon 的功能，以满足具体的业务。
- Logi-security 主要提供基础功能服务，主要分为认证、鉴权、管理三部分功能。其中认证包括了登录、注册、注销等功能；鉴权包括了确定用户的界面权限、资源权限的功能；管理包括了用户、项目、角色、部门的基本操作的功能和操作日志、消息通知的调控功能；
- Logi-job 是分布式的定时调度服务
## 1.logi-security
### 1.1 介绍
logi-security 提供项目大多都需要基础的一些功能（用户、角色、权限、登录、注册、操作记录），这些功能开发简单但是开发起来又比较繁琐和有一定的工作量（开发、测试、联调、编写接口文档等等），所以打算把这类的功能抽取出来，整合进 Logi-security，让这些项目开发人员更加专注于核心功能，避免时间花费在繁琐的基础功能的开发。
### 1.2 功能支持
主要提供：用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知
- 用户模块：提供了注册、登录、认证功能，以及用户信息的展示等基础功能；
- 项目模块：提供了创建、删除、展示、更改运行状态等功能；
- 部门模块：提供了部门信息导入、部门树形结构展示等功能；
- 界面权限模块：提供界面权限信息的导入、界面功能展示控制等功能；
- 资源权限模块：提供对项目具体资源的权限管理；
- 操作日志模块：记录用户的操作记录，展示记录；
- 消息通知模块：用户拥有的角色、拥有的资源权限变更等通知。
### 1.3 使用方式
#### 1.3.1 添加Maven
```xml
<dependency>
    <groupId>io.github.zqrferrari</groupId>
    <artifactId>logi-security-spring-boot-starter</artifactId>
    <version>1.6.3</version>
</dependency>
```
#### 1.3.2 配置文件
logi-security基于springBoot框架开发，需添加以下配置信息：
```properties
logi.security.app-name: ES
logi.security.resource-extend-bean-name: myResourceExtendImpl

# ---------------数据源信息---------------
logi.security.username=root
logi.security.password=123456
logi.security.url=jdbc:mysql://localhost:3306/logi_security?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
logi.security.driver-class-name=com.mysql.jdbc.Driver
```
- app-name：表示应用的名称，涉及到 logi-security 的数据库操作都会带上这个条件（查询、保存等操作）；
- resource-extend-bean-name：resourceExtend的实现类在spring容器bean的名称，logi-security 中资源权限管理模块，需要获取具体资源的信息，所以用户需实现 ResourceExtend 接口并指定实现类在spring容器中bean的名称；
#### 1.3.3 需要实现接口
用户可选的实现 ResourceExtend 接口，如果不实现，则资源权限管理模块的功能不能完整使用。接口详情见代码
#### 1.3.4 导入数据
logi-security相关界面并没提供【角色权限元数据、资源类别数据、部门信息数据、操作日志相关（操作页面、操作对象、对象分类）】的创建功能，logi-security提供了数据导入的接口。
建议全部都导入，简单的数据也行。
## 2.logi-job
### 2.1 介绍
是分布式的定时调度服务。
### 2.2 功能支持
主要提供：分布式定时调度服务、任务管理、分布式锁等功能
- 分布式定时调度服务：添加指定注解，并实现规定的接口，编写待调度的方法；
- 任务管理模块：提供查看任务列表、任务详情、手动执行任务、手动添加执行任务、指定任务执行 node、变更任务状态、任务日志等功能；
- 分布式锁机制：确保多系统下，对于临界资源的保护，和调节调度秩序，防饿死。
### 2.3 使用方式
#### 2.3.1 添加Maven
```xml
<dependency>
    <groupId>io.github.zqrferrari</groupId>
    <artifactId>logi-job-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```
#### 2.3.2 配置信息
logi-job基于springBoot框架开发，在使用的时候需要在配置文件中增加几项配置信息，如下：
```yaml
spring:
  logi-job:
    jdbc-url: jdbc:mysql://localhost:3306/es_manager_test?useUnicode=true&characterEncoding=utf8&jdbcCompliantTruncation=true&allowMultiQueries=true&useSSL=false
    username: root
    password: 123456
    driver-class-name: com.mysql.jdbc.Driver
    max-lifetime: 60000
    init-sql: true
    init-thread-num: 10
    max-thread-num: 20 #调度最大线程数
    log-exipre: 3  #日志保存天数，以天为单位
    app_name: arius_test02 #应用名，用户隔离机器和环境
    claim-strategy: com.didiglobal.logi.job.core.consensual.RandomConsensual #调度策略，有两种随机和广播，默认是随机
    node-name: node1 # executor node 名，须唯一
    job-log-fetcher-extend-bean-name: com.didiglobal.logi.job.extend.impl.DefaultJobLogFetcherExtendImpl # job log fetcher 名，默认从 elasticsearch 进行日志查询
    elasticsearch-address: localhost # 存储 job log 的 elasticsearch address
    elasticsearch-port: 9200 # 存储 job log 的 elasticsearch port
    elasticsearch-user: admin # 存储 job log 的 elasticsearch user
    elasticsearch-password: admin # 存储 job log 的 elasticsearch password
    elasticsearch-index-name: index_observability # 存储 job log 的 elasticsearch index
    elasticsearch-type-name: type # 存储 job log 的 elasticsearch type
```
#### 2.3.3 使用样例
```java
package com.didichuxing.datachannel.arius.admin.task.metadata;

import com.didichuxing.datachannel.arius.admin.metadata.job.cluster.monitor.esMonitorJob.MonitorJobHandler;
import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

// @Task 注解自带了 @Component
@Task(name = "esMonitorJob", description = "monitor调度任务", cron = "0 0/1 * * * ? *", autoRegister = true)
public class ESMonitorJobTask implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESMonitorJobTask.class);

    @Autowired
    private MonitorJobHandler monitorJobHandler;

    @Override
    public TaskResult execute(JobContext jobContext) throws Exception {
        LOGGER.info("class=ESMonitorJobTask||method=execute||msg=start");
        monitorJobHandler.handleJobTask("");
        return TaskResult.SUCCESS;
    }
}
```
#### 2.3.4 动态添加调度任务

```
URL：localhost:8088/v1/logi-job/task
Http Method：Post
Request Body：{
    "name": "带参数的定时任务",
    "description": "带参数的定时任务",
    "cron": "0 0/1 * * * ? *",
    "className": "com.didiglobal.logi.job.examples.task.JobBroadcasWithParamtTest", # 须预先编写好
    "params": "{\"name\":\"william\", \"age\":30}", # job 入参
    "consensual": "RANDOM",
    "nodeNameWhiteListString": "[\"node1\"]" # 该任务可运行的节点列表，对应配置文件中配置项 node-name
}
```

#### 2.3.5 查看任务执行相关上下文日志、trace信息

```
URL：localhost:8088/v1/logi-job/logs/{jobLogId}
Http Method：GET
```

## 3.logi-log
### 3.1 介绍
集成了：logi-log-log、logi-log-log4j2。
### 3.2 添加maven
```xml
<dependency>
	<groupId>io.github.zqrferrari</groupId>
	<artifactId>logi-log</artifactId>
	<version>2.0.0</version>
</dependency>
```
### 3.2 logi-log
logi-log是基于slf4j封装的组件，为用户提供日志相关功能。各个业务可以选择log4j，logback，log4j2，只要配置上桥接就可以使用。
#### 3.2.1 日志聚合
1. 日志聚合

   是为了防止频繁打印日志，影响应用的运行，特别是在异常场景下，每条数据都会触发异常。聚合是通过key来实现聚合的，可以自定义key来实现多种聚合。

   ```java
   LogGather.recordErrorLog("myKey", "fail to parse xxx");
   ```

2. 日志采样

   ```java
   LogGather.recordInfoLog("myKey", "this is log");
   ```

### 3.3 logi-log-log4j2
logi-log-log4j2，是基于log4j2 2.9.1封装的，支持日志发送到kafka，以及过滤重复日志功能。
#### 3.3.1 日志发送到kafka
配置appender，appName设置为唯一的

```xml
<Appenders>
    <Kafka name="kafka" topic="${log.kafka.topic}" syncSend="false">
        <SimpleMqLogEventPatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %L - %msg%xEx%n"
                                       appName="${log.app.name}"/>
        <Property name="bootstrap.servers">
            ${log.kafka.bootstrap}
        </Property>
    </Kafka>
 
</Appenders>
<Loggers>
    <logger name="errorLogger" additivity="false">
        <level value="error"/>
        <AppenderRef ref="errorLogger"/>
        <AppenderRef ref="kafka"/>
    </logger>
</Loggers>
```
也可以选择直接发送原始日志：

```xml
<Kafka name="kafka" topic="${log.kafka.topic}" syncSend="false">
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %L - %msg%xEx%n"/>
    <Property name="bootstrap.servers">
        ${log.kafka.bootstrap}
    </Property>
</Kafka>
```
#### 3.3.2 过滤重复日志
配置appender即可

```xml
<NoRepeatRollingFile name="testDRollingFile" fileName="logs/detectLogger.log"
                       filePattern="logs/detectLogger.log.%i" append="true">
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %L - %msg%xEx%n"/>
    <SizeBasedTriggeringPolicy size="10MB"/>
    <DefaultRolloverStrategy max="5"/>
</NoRepeatRollingFile>
```

#### 3.3.3 日志发送到 Elasticsearch

```
<Appenders>
		<ElasticsearchAppender name="esAppender" address="localhost" port="9200" user="admin" password="admin" indexName="index_observability" typeName="type" threshold="all" bufferSize="1000" numberOfShards="1" numberOfReplicas="1">
		</ElasticsearchAppender>
</Appenders>
<Loggers>
	<root level="INFO">
		<appender-ref ref="esAppender" />
	</root>
</Loggers>
```

## 4.logi-metrices

Arius内部指标采集和计算的工具包。
### 4.1 添加Maven
```xml
<dependency>
    <groupId>io.github.zqrferrari</groupId>
    <artifactId>logi-metrices</artifactId>
    <version>1.0.4</version>
</dependency>
```
## 5.logi-dsl-prase
用于解析dsl语法树的组件，用于解析用户查询的dsl，生成dsl模板，用于gatewayjoin日志的聚合，dsl限流等场景。
### 5.1 添加Maven
```xml
<dependency>
    <groupId>io.github.zqrferrari</groupId>
    <artifactId>logi-dsl-prase</artifactId>
    <version>1.0.7</version>
</dependency>
```
## 6.logi-elasticsearch-client
### 6.1 添加Maven
```xml
<dependency>
    <groupId>io.github.zqrferrari</groupId>
    <artifactId>logi-elasticsearch-client</artifactId>
    <version>1.0.1</version>
</dependency>
```
## 7.logi-elasticsearch-sql
### 7.1 添加Maven
```xml
<dependency>
    <groupId>io.github.zqrferrari</groupId>
    <artifactId>logi-elasticsearch-sql</artifactId>
    <version>1.0.4</version>
</dependency>
```

## 8.logi-observability

基于 open-telemetry 规范的可观性 SDK 组件库，提供快速接入可观测性的能力。

### 8.1添加Maven

```
<dependency>
	<groupId>io.github.zqrferrari</groupId>
	<artifactId>logi-observability</artifactId>
	<version>1.0.0</version>
</dependency>
```

### 8.2.2组件使用

#### 8.2.2.1 HttpClient组件

根据需要，调用HttpUtils类对应方法，即可自动在Http头注入对应符合OpenTelemetry规范的上下文信息，如：

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

#### 8.2.2.2 Servlet Filter 组件

作为Http网关拦截入口，用于解析 & 注入Http头部符合OpenTelemetry规范的上下文信息，构建对应span，并对请求处理过程是否出现异常与非200状态码进行监控。

##### 8.2.2.2.1集成方式

需要将 com.didiglobal.logi.observability.conponent.spring.filter.ObservabilityFilter 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {
	
}
```

**需要注意的是，Filter 仅对 Http 响应状态码为非 200，进行错误标注，对于应用内置状态码不做对应校验控制**。

#### 8.2.2.3 Spring AOP 组件

作为对Spring应用内部方法调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对方法处理过程是否出现异常进行监控。

##### 8.2.2.3.1集成方式

需要将 com.didiglobal.logi.observability.conponent.spring.aop.ConfigurableAdvisorConfig 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {
	
}
```

#### 8.2.2.4 Mybatis Interceptor 组件

作为对Mybatis内部SQL调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对SQL调用过程是否出现异常进行监控。

##### 8.2.2.4.1集成方式

###### 8.2.2.4.1.1 纯 Mabatis 应用

将 com.didiglobal.logi.observability.conponent.mybatis.ObservabilityInterceptor 添加进 SqlSessionFactory Configuration 中的 interceptorChain 中，如：

```java
SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
bean.setDataSource(dataSource);
bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
SqlSessionFactory sqlSessionFactory = bean.getObject(); // 设置mybatis的xml所在位置
sqlSessionFactory.getConfiguration().addInterceptor(new ObservabilityInterceptor());
return sqlSessionFactory;
```

###### 8.2.2.4.1.2 Spring + MyBatis

需要将 com.didiglobal.logi.observability.conponent.mybatis.ApplicationMybatisInjectionListener 类处于 spring 可扫描范围，如：

```
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager", "com.didiglobal.logi"})
public class AgentManagerApplication {
	
}
```

#### 8.2.2.5 Thread 组件

作为线程池创建入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对线程运行方法处理过程是否出现异常进行监控。支持两种类型的线程池接口：

1. ExecutorService
2. ScheduledExecutorService

支持基于返回值 Future 的多个不同线程的上下文串联。

##### 8.2.2.5.1 线程池用法

```java
package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.ContextFuture;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.SneakyThrows;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.*;

public class ContextExecutorServiceTestInDiffParentThread {

    private static Tracer tracer = Observability.getTracer(ContextExecutorServiceTestInDiffParentThread.class.getName());

    private static final ILog logger = LogFactory.getLog(ContextExecutorServiceTestInDiffParentThread.class);

    public static void main(String[] args) throws InterruptedException {

        //1.）封装线程 池
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

        Future<String> future = null;
        Span span = tracer.spanBuilder("main").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("start function main()");
            //2.）提交附带返回值任务
            future = threadPool1.submit(new MyCallable());
        } finally {
            span.end();
        }

        //3.）将范围值作为入参，新线程执行
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
                ContextFuture contextFuture = (ContextFuture) future;
                String msg = contextFuture.get().toString();
                contextFuture.getContext().makeCurrent();
                Span span = tracer.spanBuilder("MyRunnable.run()").startSpan();
                try(Scope scope = span.makeCurrent()) {
                    logger.info("MyRunnable.run()");
                    logger.info(" parameter is : " + msg);
                } finally {
                    span.end();
                }
            } catch (Exception ex) {

            }
        }
    }

}

```

```java
package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.ContextFuture;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.SneakyThrows;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.*;

public class ContextExecutorServiceTestInSameParentThread {

    private static Tracer tracer = Observability.getTracer(ContextExecutorServiceTestInSameParentThread.class.getName());

    private static final ILog logger = LogFactory.getLog(ContextExecutorServiceTestInSameParentThread.class);

    public static void main(String[] args) throws InterruptedException {

        //1.）封装线程 池
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

        Span span = tracer.spanBuilder("main").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("start function main()");
            //2.）提交附带返回值任务
            Future<String> future = threadPool1.submit(new MyCallable());
            //3.）将范围值作为入参，新线程执行
            threadPool2.submit(new MyRunnable(future));
        } finally {
            span.end();
        }

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
                ContextFuture contextFuture = (ContextFuture) future;
                String msg = contextFuture.get().toString();
                logger.info("MyRunnable.run()");
                logger.info(" parameter is : " + msg);
            } catch (Exception ex) {

            }
        }
    }

}

```

```java
package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.ContextScheduledFuture;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.SneakyThrows;

import java.util.concurrent.*;

public class ContextScheduledExecutorServiceTestInDiffParentThread {

    private static Tracer tracer = Observability.getTracer(ContextScheduledExecutorServiceTestInDiffParentThread.class.getName());

    private static final ILog logger = LogFactory.getLog(ContextScheduledExecutorServiceTestInDiffParentThread.class);

    public static void main(String[] args) throws InterruptedException {

        //1.）封装线程 池
        ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));

        ScheduledFuture<String> scheduledFuture = null;
        Span span = tracer.spanBuilder("main").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("start function main()");
            //2.）提交附带返回值任务
            scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
        } finally {
            span.end();
        }

        //3.）将范围值作为入参，新线程执行
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

    static class MyRunnable implements Runnable {
        private ScheduledFuture scheduledFuture;
        public MyRunnable(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
        @SneakyThrows
        @Override
        public void run() {
            try {
                ContextScheduledFuture contextFuture = (ContextScheduledFuture) scheduledFuture;
                String msg = contextFuture.get().toString();
                contextFuture.getContext().makeCurrent();
                Span span = tracer.spanBuilder("MyRunnable.run()").startSpan();
                try(Scope scope = span.makeCurrent()) {
                    logger.info("MyRunnable.run()");
                    logger.info(" parameter is : " + msg);
                } finally {
                    span.end();
                }
            } catch (Exception ex) {

            }
        }
    }

}

```

```java
package com.didiglobal.logi.observability.thread;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.observability.Observability;
import com.didiglobal.logi.observability.conponent.thread.ContextScheduledFuture;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.SneakyThrows;

import java.util.concurrent.*;

public class ContextScheduledExecutorServiceTestInSameParentThread {

    private static Tracer tracer = Observability.getTracer(ContextScheduledExecutorServiceTestInSameParentThread.class.getName());

    private static final ILog logger = LogFactory.getLog(ContextScheduledExecutorServiceTestInSameParentThread.class);

    public static void main(String[] args) throws InterruptedException {

        //1.）封装线程 池
        ScheduledExecutorService threadPool1 = Observability.wrap(Executors.newScheduledThreadPool(1));
        ScheduledExecutorService threadPool2 = Observability.wrap(Executors.newScheduledThreadPool(1));

        Span span = tracer.spanBuilder("main").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("start function main()");
            //2.）提交附带返回值任务
            ScheduledFuture<String> scheduledFuture = threadPool1.schedule(new MyCallable(), 0, TimeUnit.MINUTES);
            //3.）将范围值作为入参，新线程执行
            threadPool2.scheduleWithFixedDelay(new MyRunnable(scheduledFuture),10, 5 * 1000 * 60, TimeUnit.SECONDS);
        } finally {
            span.end();
        }

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
                ContextScheduledFuture contextFuture = (ContextScheduledFuture) scheduledFuture;
                String msg = contextFuture.get().toString();
                logger.info("MyRunnable.run()");
                logger.info(" parameter is : " + msg);
            } catch (Exception ex) {

            }
        }
    }

}

```

