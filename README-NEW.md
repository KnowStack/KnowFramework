# 项目概述
KnowFramework 公共模块，包括Kf-security、Kf-job 。KnowFramework 会尽量保持与具体业务的解耦，以简单易用，轻量高效，尽可能减少用户感知的方式做到多平台适配，同时提供可扩展的接口，让用户自由的扩展 KnowFramework 的功能，以满足具体的业务。
- Kf-security 主要提供基础功能服务，主要分为认证、鉴权、管理三部分功能。其中认证包括了登录、注册、注销等功能；鉴权包括了确定用户的界面权限、资源权限的功能；管理包括了用户、项目、角色、部门的基本操作的功能和操作日志、消息通知的调控功能；
- Kf-job 是分布式的定时调度服务

## [1. kf-security](./kf-security-spring-boot-starter/README.md)
### 1.1 介绍
kf-security 提供项目大多都需要基础的一些功能（用户、角色、权限、登录、注册、操作记录），这些功能开发简单但是开发起来又比较繁琐和有一定的工作量（开发、测试、联调、编写接口文档等等），所以打算把这类的功能抽取出来，整合进 kf-security，让这些项目开发人员更加专注于核心功能，避免时间花费在繁琐的基础功能的开发。
### 1.2 功能支持
主要提供：用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知
- 用户模块：提供了注册、登录、认证功能，以及用户信息的展示等基础功能；
- 项目模块：提供了创建、删除、展示、更改运行状态等功能；
- 部门模块：提供了部门信息导入、部门树形结构展示等功能；
- 界面权限模块：提供界面权限信息的导入、界面功能展示控制等功能；
- 资源权限模块：提供对项目具体资源的权限管理；
- 操作日志模块：记录用户的操作记录，展示记录；
- 消息通知模块：用户拥有的角色、拥有的资源权限变更等通知。

## [2. kf-job](./kf-job-spring-boot-starter/README.md)
### 2.1 介绍
是分布式的定时调度服务。
### 2.2 功能支持
主要提供：分布式定时调度服务、任务管理、分布式锁等功能
- 分布式定时调度服务：添加指定注解，并实现规定的接口，编写待调度的方法；
- 任务管理模块：提供查看任务列表、任务详情、手动执行任务、手动添加执行任务、指定任务执行 node、变更任务状态、任务日志等功能；
- 分布式锁机制：确保多系统下，对于临界资源的保护，和调节调度秩序，防饿死。

## [3. kf-log](./kf-log/README.md)

### 3.1 介绍
集成了：kf-log:ILog、kf-log：log4j2。

### 3.2 功能支持
- ILog：
    ILog是基于slf4j封装的组件，为用户提供日志相关功能，如日志聚合、日志采样等等。各个业务可以选择log4j，logback，log4j2，只要配置上桥接就可以使用。
    1. 日志聚合：为了防止频繁打印日志，影响应用的运行，特别是在异常场景下，每条数据都会触发异常。聚合是通过key来实现聚合的，可以自定义key来实现多种聚合。 
    2. 日志采样

- log4j2：
    kf-log提供的log4j2，是基于log4j2 2.17.1封装的，支持日志发送到kafka、ES，以及过滤重复日志功能。

    1. Kafka：日志发送到kafka
    2. NoRepeatRollingFile：过滤重复日志
    3. ElasticsearchAppender：日志发送到 Elasticsearch

## [4. kf-metrices](./kf-metrices/README.md)
Arius内部指标采集和计算的工具包。

## [5. kf-dsl-prase](./kf-dsl-prase/README.md)
用于解析dsl语法树的组件，用于解析用户查询的dsl，生成dsl模板，用于gatewayjoin日志的聚合，dsl限流等场景。

## [6. kf-elasticsearch-client](./kf-elasticsearch-client/README.md)
提供了elasticsearch多版本兼容的查询客户端。

## [7. kf-elasticsearch-sql](./kf-elasticsearch-sql/README.md)

用于解析sql语法，进行elasticsearch查询。

## [8. kf-observability](./kf-observability/README.md)
### 8.1 介绍
基于 open-telemetry 规范的可观性 SDK 组件库，提供快速接入可观测性的能力。

### 8.2 功能支持

- HttpClient组件:
    
    根据需要，调用HttpUtils类对应方法，即可自动在Http头注入对应符合OpenTelemetry规范的上下文信息。

- Servlet Filter 组件

    作为Http网关拦截入口，用于解析 & 注入Http头部符合OpenTelemetry规范的上下文信息，构建对应span，并对请求处理过程是否出现异常与非200状态码进行监控。

    **需要注意的是，Filter 仅对 Http 响应状态码为非 200，进行错误标注，对于应用内置状态码不做对应校验控制**。

- Spring AOP 组件

    作为对Spring应用内部方法调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对方法处理过程是否出现异常进行监控。


- Mybatis Interceptor 组件

    作为对Mybatis内部SQL调用拦截入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对SQL调用过程是否出现异常进行监控。

- Thread 组件

    作为线程池创建入口，用于解析 & 注入符合OpenTelemetry规范的上下文信息，构建对应span，并对线程运行方法处理过程是否出现异常进行监控。支持基于返回值 Future 的多个不同线程的上下文串联、支持两种类型的线程池接口：

    1. ExecutorService
    2. ScheduledExecutorService
    
    