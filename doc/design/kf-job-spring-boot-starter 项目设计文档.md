# kf-job-spring-boot-starter 项目设计文档

## 1. E-R 图

![kf-job-e-r](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/kf-job-e-r.png)

### 1.1 实体模型介绍

#### logi_task

表示一个定时任务的定义，定时任务定义的相关信息如下：

|          属性名          |                             描述                             |
| :----------------------: | :----------------------------------------------------------: |
|            id            |                    主键，自增，无业务意义                    |
|        task_code         |                     定时任务定义唯一标识                     |
|        task_name         |                         定时任务名称                         |
|        task_desc         |                       定时任务任务描述                       |
|           cron           |                   定时任务对应 cron 表达式                   |
|        class_name        |                     定时任务类的全限定名                     |
|          params          | 定时任务对应任务执行参数，格式：map {key1:value1,key2:value2} |
|       retry_times        |                   定时任务对应允许重试次数                   |
|      last_fire_time      |                   定时任务对应上次执行时间                   |
|         timeout          |               定时任务对应超时阈值，单位：毫秒               |
|          status          |               定时任务对应状态：0暂停 1运行中                |
|      sub_task_codes      |           定时任务对应子任务对应code列表，逗号分隔           |
|        consensual        | 定时任务对应执行策略，支持可扩展，目前仅提供两种默认方式：随机抢占式与广播式 |
|          owner           |                            责任人                            |
|     task_worker_str      | 定时任务对应执行节点信息，格式：json List<LogITask.TaskWorker> |
|         app_name         |                           应用名称                           |
| node_name_white_list_str |     定时任务对应执行器节点名的白名单集，格式：json List      |
|       create_time        |                     定时任务对应创建时间                     |
|       update_time        |                  定时任务对应近一次更新时间                  |

#### logi_job

表示一个定时任务的实例，对应一个正在执行的定时任务，定时任务实例的相关信息如下：

|   属性名    |                             描述                             |
| :---------: | :----------------------------------------------------------: |
|     id      |                    主键，自增，无业务意义                    |
|  job_code   |                     定时任务实例唯一标识                     |
|  task_code  | 定时任务实例对应定时任务定义的唯一标识，对应 logi_task.task_code |
| class_name  |                     定时任务类的全限定名                     |
|  try_times  |                           重试次数                           |
| worker_code | 运行该定时任务实例的执行器对应唯一标识，对应 logi_worker.worker_code |
|  app_name   |                           应用名称                           |
| start_time  |                   定时任务实例开始运行时间                   |
| create_time |                   定时任务实例对应创建时间                   |
| update_time |                定时任务实例对应近一次更新时间                |

#### logi_worker

表示一个定时任务执行器，定时任务执行器的相关信息如下：

|     属性名      |               描述                |
| :-------------: | :-------------------------------: |
|       id        |      主键，自增，无业务意义       |
|   worker_code   |          执行器唯一标识           |
|   worker_name   |            执行器名称             |
|       ip        |      执行器所在主机 ip 地址       |
|       cpu       |    执行器所在主机 cpu 逻辑核数    |
|    cpu_used     |     执行器所在主机 cpu 使用率     |
|     memory      | 执行器所在主机内存大小，单位：MB  |
|   memory_used   |     执行器所在主机内存使用率      |
|   jvm_memory    | 执行器对应 JVM 内存大小，单位：MB |
| jvm_memory_used |     执行器对应 JVM 内存使用率     |
|     job_num     |      执行器正在运行的 job 数      |
|    heartbeat    |       执行器近一次心跳时间        |
|    app_name     |             应用名称              |
|   create_time   |        执行器对应创建时间         |
|   update_time   |     执行器对应近一次更新时间      |
|    node_name    |         执行器对应节点名          |

#### logi_task_lock

表示一个定时任务锁，定时任务锁的相关信息如下：

|   属性名    |                     描述                     |
| :---------: | :------------------------------------------: |
|     id      |            主键，自增，无业务意义            |
|  task_code  | 定时任务的唯一标识，对应 logi_task.task_code |
| worker_code | 执行器唯一标识，对应 logi_worker.worker_code |
|  app_name   |                   应用名称                   |
| expire_time |             每次申请锁的过期时间             |
| create_time |            定时任务锁对应创建时间            |
| update_time |         定时任务锁对应近一次更新时间         |

#### logi_job_log

表示一条定时任务实例运行的日志，定时任务实例运行日志的相关信息如下：

|         属性名         |                             描述                             |
| :--------------------: | :----------------------------------------------------------: |
|           id           |                    主键，自增，无业务意义                    |
|        job_code        |         定时任务实例唯一标识，对应 logi_job.job_code         |
|       task_code        |         定时任务的唯一标识，对应 logi_task.task_code         |
|       task_name        |                         定时任务名称                         |
|       task_desc        |                         定时任务描述                         |
|        task_id         |                定时任务 id，对应 logi_task.id                |
|       class_name       |                     定时任务类的全限定名                     |
|       try_times        |                           重试次数                           |
|      worker_code       | 运行该定时任务实例的执行器对应唯一标识，对应 logi_worker.worker_code |
|       worker_ip        |  运行该定时任务实例的执行器对应主机 ip，对应 logi_worker.ip  |
|       start_time       |                  定时任务实例的开始运行时间                  |
|        end_time        |                  定时任务实例的运行结束时间                  |
|         status         |         定时任务实例的执行结果状态 1成功 2失败 3取消         |
|         error          |      定时任务实例的执行时的错误信息（如存在错误情况下）      |
|         result         |                  定时任务实例的执行结果信息                  |
|        app_name        |                           应用名称                           |
|      create_time       |               定时任务实例运行日志对应创建时间               |
| update_timeupdate_time |            定时任务实例运行日志对应近一次更新时间            |

#### logi_worker_blacklist

表示一条执行器黑名单记录，执行器黑名单记录的相关信息如下：

|   属性名    |                       描述                       |
| :---------: | :----------------------------------------------: |
|     id      |              主键，自增，无业务意义              |
| worker_code | 执行器对应唯一标识，对应 logi_worker.worker_code |
| create_time |           执行器黑名单记录对应创建时间           |
| update_time |        执行器黑名单记录对应近一次更新时间        |

## 2. 调度引擎核心流程

​	调度引擎集群架构是对等架构而非主从架构，每个执行节点也可称作一个 worker，任务的调度执行策略支持`随机抢占执行`与`广播执行`两种模式。

### 2.1 定时任务的创建

​	定时任务的创建与配置更新，可通过编写自定义 Job 类（Job 为普通 Spring Bean）时通过添加注解`@Task`方式实现，也可通过 API 结合编写的自定义 Job 类实现，API 方式的实现很简单，这里说明一下注解方式的实现逻辑。

#### 2.1.1 自定义 Job 加载流程 - 注解方式

​	kf-job-spring-boot-starter 项目是一个 Spring 项目，自定义 job 加载流程借助 Spring 提供的 BeanPostProcessor 接口能力，在各 Bean 初始化完成后执行如下流程：

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/custom-job-load-flow.png" alt="image-20230204165239000" style="zoom: 50%;" />

### 2.2 调度引擎的运行

调度引擎启动以后将运行如下 6 个周期性任务构成，这 6 个周期性任务构成了调度引擎的运行全部。

1. worker 心跳维护任务
2. 定时任务维护流程
3. Job 执行流程
4. Job 清理流程
5. 任务锁续约流程
6. 定时任务实例运行的日志清理流程

#### 2.2.1 worker 心跳维护流程

worker 心跳维护是一个周期性动作，周期为10秒，其每次执行流程如下：

##### 2.2.1.1 主线流程

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/beat-maintain-flow.png" alt="beat-maintain-flow" style="zoom:50%;" />

worker 记录在表 logi_worker 中不存在的情况如下：

1. worker 停止
2. worker 距当前时间超 30 秒内不存在心跳

##### 2.2.1.2 分支流程 - 无心跳 worker 清理流程

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/non-beat-worker-clean-flow.png" alt="non-beat-worker-clean-flow" style="zoom:50%;" />

#### 2.2.2 定时任务维护流程

定时任务维护是一个周期性动作，周期为10秒，其每次执行流程如下：

##### 2.2.2.1 主线流程

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/task-manage-flow.png" alt="task-manage-flow" style="zoom: 67%;" />

##### 2.2.2.2 分支流程 - 获取接下来需要执行的定时任务集流程

![next-triggers-flow](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/next-triggers-flow.png)

##### 2.2.2.3 分支流程 - 定时任务在当前执行节点是否可运行流程

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/task-can-execute.png" alt="task-can-execute" style="zoom:67%;" />

##### 2.2.2.4 分支流程 - 定时任务启动、执行流程

![task-start](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/task-start.png)

#### 2.2.3 Job 执行流程

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/job-execute-flow.png" alt="job-execute-flow" style="zoom:67%;" />

自定义 Job 运行过程中出现 InterruptedException 类型异常的情况如下：

- 任务执行超时后的中断处理
- 通过 API 停止对应任务实例
- 通过 API 更改该任务实例对应任务定义状态为 STOP
- 调度引擎停止

#### 2.2.4 Job 清理流程

Job 清理是一个周期性动作，周期为10秒，其每次执行流程如下：

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/job-reorganize-flow.png" alt="job-reorganize-flow"  />

#### 2.2.5 任务锁续约流程

任务锁续约是一个周期性动作，周期为10秒，其每次执行流程如下：

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/lock-renew-flow.png" alt="lock-renew-flow" style="zoom: 50%;" />

#### 2.2.6 定时任务实例运行的日志清理流程

定时任务实例运行日志清理是一个周期性动作，周期为1 小时，其每次执行流程如下：

<img src="https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/log-clean-flow.png" alt="log-clean-flow" style="zoom:50%;" />

### 2.3 调度引擎的停止

#### 2.3.1 主线流程

![scheduler-stop](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/scheduler-stop.png)

#### 2.3.2 分支流程 - 停止 worker 心跳维护任务的运行

![beat-maintain-task-stop](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/beat-maintain-task-stop.png)

#### 2.3.3 分支流程 - 停止定时任务维护任务的运行

![task-maintain-stop](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/task-maintain-stop.png)

##### 2.3.3.1 每一个正在运行的定时任务停止流程

![job-stop](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/job-stop.png)

