# 项目概述
LogI-common 即公共模块，主要是给其他项目提供通用的服务。主要的服务包括 LogI-security、LogI-job 等等。LogI-common 会尽量保持与具体业务的解耦，以简单易用，轻量高效，尽可能减少用户感知的方式做到多平台适配，同时提供可扩展的接口，让用户自由的扩展 LogI-common 的功能，以满足具体的业务。
- LogI-security 主要提供基础功能服务，主要分为认证、鉴权、管理三部分功能。其中认证包括了登录、注册、注销等功能；鉴权包括了确定用户的界面权限、资源权限的功能；管理包括了用户、项目、角色、部门的基本操作的功能和操作日志、消息通知的调控功能；
- LogI-job 是分布式的定时调度服务，主要用于ESManager项目。
## 1.logi-security
### 1.1 介绍
LogI-security 考虑到大多数的 Manager 类项目的场景，结合滴滴云商业数据的开源项目的立项和开发，例如 ESManager、KafkaManager、AgentManager 等等，发现这类项目大多都需要基础的一些功能（用户、角色、权限、登录、注册、操作记录），这些功能开发简单但是开发起来又比较繁琐和有一定的工作量（开发、测试、联调、编写接口文档等等），所以打算把这类的功能抽取出来，整合进 LogI-security，让这些项目开发人员更加专注于核心功能，避免时间花费在繁琐的基础功能的开发。
### 1.2 功能支持
主要提供：用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知
- 用户模块：提供了注册、登录、认证功能，以及用户信息的展示等基础功能；
- 项目模块：提供了创建、删除、展示、更改运行状态等功能；
- 部门模块：提供了部门信息导入、部门树形结构展示等功能；
- 界面权限模块：提供界面权限信息的导入、界面功能展示控制等功能；
- 资源权限模块：提供对EM、KM、AM项目具体资源的权限管理；
- 操作日志模块：记录用户的操作记录，展示记录；
- 消息通知模块：用户拥有的角色、拥有的资源权限变更等通知。
### 1.3 使用方式
#### 1.3.1 添加Maven
```xml
<dependency>
    <groupId>com.didiglobal.logi</groupId>
    <artifactId>logi-security-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
#### 1.3.2 配置文件
logi-security基于springBoot框架开发，需添加以下配置信息：
```properties
logi.security.app-name: ES
logi.security.resource-extend-bean-name: myResourceExtendImpl

# ---------------数据源信息---------------
logi.security.username=didi
logi.security.password=123456
logi.security.url=jdbc:mysql://localhost:3306/logi_security?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
logi.security.driver-class-name=com.mysql.jdbc.Driver
```
- app-name：表示应用的名称，涉及到 logi-security 的数据库操作都会带上这个条件（查询、保存等操作）；
- resource-extend-bean-name：resourceExtend的实现类在spring容器bean的名称，logi-security 中资源权限管理模块，需要获取具体资源的信息，所以用户需实现 ResourceExtend 接口并指定实现类在spring容器中bean的名称；
#### 1.3.3 需要实现接口
用户可选的实现 ResourceExtend 接口，如果不实现，则资源权限管理模块的功能不能完整使用。接口详情见代码
```java
package com.didiglobal.logi.security.extend;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.resource.ResourceDTO;

import java.util.List;

/**
 * 资源扩展接口
 */
public interface ResourceExtend {

    /**
     * 获取资源信息List，资源id指的是该资源所在服务对该资源的标识
     * @param projectId 项目id（可为null）
     * @param resourceTypeId 资源类型id（可为null，不为null则projectId必不为null）
     * @param resourceName 资源名称（可为null，模糊查询条件）
     * @param page 当前页（分页条件）
     * @param size 页大小（分页条件）
     * @return 资源信息List
     */
    PagingData<ResourceDTO> getResourcePage(Integer projectId, Integer resourceTypeId, String resourceName, int page, int size);

    /**
     * 获取资源信息List，资源id指的是该资源所在服务对该资源的标识
     * @param projectId 项目id（可为null）
     * @param resourceTypeId 资源类型id（可为null，不为null则projectId必不为null）
     * @return 资源信息List
     */
    List<ResourceDTO> getResourceList(Integer projectId, Integer resourceTypeId);

    /**
     * 获取具体资源个数，资源id指的是该资源所在服务对该资源的标识
     * @param projectId 项目id（可为null）
     * @param resourceTypeId 资源类型id（可为null，不为null则projectId必不为null）
     * @return 资源信息List
     */
    int getResourceCnt(Integer projectId, Integer resourceTypeId);
}
```
#### 1.3.4 导入数据
logi-security相关界面并没提供【角色权限元数据、资源类别数据、部门信息数据、操作日志相关（操作页面、操作对象、对象分类）】的创建功能，logi-security提供了数据导入的接口。

建议全部都导入，简单的数据也行。

接口详情见API文档。
## 2.logi-job
### 2.1 介绍
是分布式的定时调度服务，主要用于ESManager项目。
### 2.2 使用方式
#### 2.2.1 添加Maven
```xml
<dependency>
    <groupId>com.didiglobal.logi</groupId>
    <artifactId>logi-job-spring-boot-starter</artifactId>
    <version>1.6-SNAPSHOT</version>
</dependency>
```
#### 2.2.2 配置信息
logi-job基于springBoot框架开发，在使用的时候需要在配置文件中增加几项配置信息，如下：
```yaml
spring:
logi-job:
  jdbc-url: jdbc:mysql://localhost:4859/es_manager_test?useUnicode=true&characterEncoding=utf8&jdbcCompliantTruncation=true&allowMultiQueries=true&useSSL=false
  username: didi
  password: 123456
  driver-class-name: com.mysql.jdbc.Driver
  max-lifetime: 60000
  init-sql: true
  init-thread-num: 10
  max-thread-num: 20 #调度最大线程数
  log-exipre: 3  #日志保存天数，以天为单位
  app_name: arius_test02 #应用名，用户隔离机器和环境
  claim-strategy: com.didiglobal.logi.job.core.consensual.RandomConsensual #调度策略，有两种随机和广播，默认是随机
```
#### 2.2.3 使用样例
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
