# 项目概述
KnowFramework是滴滴基于开源技术组件构建Paas服务体系过程中沉淀的业务无关通用技术能力组件库。组件设计遵从简单易用，轻量高效，低耦合、高扩展的设计理念，核心组件如下：
- 基于 open-telemetry 规范构建的可观性 SDK 组件库：[kf-observability](./kf-observability)
- 基于quarz+mysql构建的分布式定时调度组件库：[kf-job](./kf-job-spring-boot-starter)
- 基于资源、权限、角色构建的权限管控组件库：[kf-security](./kf-security-spring-boot-starter)


## [1. kf-observability](./kf-observability)
### 1.1 介绍

遵从open-telemetry 规范的可观性SDK组件库，融入Grafana生态，为业务应用提供低门槛的Metrics+Log+Trace观测能力。详情参见：[kf-observability](./kf-observability)/[README.md](./kf-observability/README.md)

### 1.2 功能支持

- HttpClient组件:

  根据需要，调用HttpUtils类对应方法，即可自动在Http头注入对应符合OpenTelemetry规范的上下文信息。

- Servlet Filter 组件

  作为Http网关拦截入口，用于解析 & 注入Http头部符合OpenTelemetry规范的上下文信息，构建对应span，并对请求处理过程是否出现异常与非200状态码进行监控。

  **Filter 仅对 Http 响应状态码为非 200，进行错误标注，对于应用内置状态码不做对应校验控制**。

- Spring AOP 组件

  作为对Spring应用内部方法调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对方法处理过程是否出现异常进行监控。


- Mybatis Interceptor 组件

  作为对Mybatis内部SQL调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对SQL调用过程是否出现异常进行监控。

- Thread 组件

  作为线程池创建入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对线程运行方法处理过程是否出现异常进行监控。支持基于返回值 Future 的多个不同线程的上下文串联、支持两种类型的线程池接口：

   1. ExecutorService
   2. ScheduledExecutorService

## [2. kf-job](./kf-job-spring-boot-starter)
### 2.1 介绍
基于quarz+mysql构建的分布式定时调度服务。详情参见：[kf-job-spring-boot-starter](./kf-job-spring-boot-starter)/[README.md](./kf-job-spring-boot-starter/README.md)
### 2.2 功能支持
主要提供：分布式定时调度服务、任务管理、分布式锁等功能
- 分布式定时调度服务：添加指定注解，并实现规定的接口，编写待调度的方法；
- 任务管理模块：提供查看任务列表、任务详情、手动执行任务、手动添加执行任务、指定任务执行 node、变更任务状态、任务日志等功能；
- 分布式锁机制：确保多系统下，对于临界资源的保护，和调节调度秩序，防饿死。


## [3. kf-security](./kf-security-spring-boot-starter)
### 3.1 介绍
kf-security 提供PaaS平台基础功能（用户、角色、权限、登录、注册、操作记录），详情参见：[kf-security-spring-boot-starter](./kf-security-spring-boot-starter)/[README.md](./kf-security-spring-boot-starter/README.md)
### 3.2 功能支持
主要提供：用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知
- 用户模块：提供了注册、登录、认证功能，以及用户信息的展示等基础功能；
- 项目模块：提供了创建、删除、展示、更改运行状态等功能；
- 部门模块：提供了部门信息导入、部门树形结构展示等功能；
- 界面权限模块：提供界面权限信息的导入、界面功能展示控制等功能；
- 资源权限模块：提供对项目具体资源的权限管理；
- 操作日志模块：记录用户的操作记录，展示记录；
- 消息通知模块：用户拥有的角色、拥有的资源权限变更等通知。


## [4. kf-log](./kf-log)

### 4.1 介绍
know-framework日志组件，集成了：kf-log:ILog、kf-log：log4j2。详情参见：[kf-log](./kf-log)/[README.md](./kf-log/README.md)

### 4.2 功能支持
- ILog：
  ILog是基于slf4j封装的组件，为用户提供日志相关功能，如日志聚合、日志采样等等。各个业务可以选择log4j，logback，log4j2，只要配置上桥接就可以使用。

- log4j2：
  kf-log提供的log4j2，是基于log4j2 2.17.1封装的，支持日志发送到kafka、ES，以及过滤重复日志功能。
    
