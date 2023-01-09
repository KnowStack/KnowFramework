## 2.kf-job
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
    <groupId>io.github.knowstack</groupId>
    <artifactId>kf-job-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### 2.3.2 配置信息
kf-job基于springBoot框架开发，在使用的时候需要在配置文件中增加几项配置信息，如下：
```yaml
spring:
  kf-job:
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
    claim-strategy: com.didiglobal.knowframework.job.core.consensual.RandomConsensual #调度策略，有两种随机和广播，默认是随机
    node-name: node1 # executor node 名，须唯一
    job-log-fetcher-extend-bean-name: com.didiglobal.knowframework.job.extend.impl.DefaultJobLogFetcherExtendImpl # job log fetcher 名，默认从 elasticsearch 进行日志查询
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
import com.didiglobal.knowframework.job.annotation.Task;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.job.core.job.Job;
import com.didiglobal.knowframework.job.core.job.JobContext;
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
URL：localhost:8088/v1/kf-job/task
Http Method：Post
Request Body：{
    "name": "带参数的定时任务",
    "description": "带参数的定时任务",
    "cron": "0 0/1 * * * ? *",
    "className": "com.didiglobal.knowframework.job.examples.task.JobBroadcasWithParamtTest", # 须预先编写好
    "params": "{\"name\":\"william\", \"age\":30}", # job 入参
    "consensual": "RANDOM",
    "nodeNameWhiteListString": "[\"node1\"]" # 该任务可运行的节点列表，对应配置文件中配置项 node-name
}
```

#### 2.3.5 查看任务执行相关上下文日志、trace信息

```
URL：localhost:8088/v1/kf-job/logs/{jobLogId}
Http Method：GET
```
