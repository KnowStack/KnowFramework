# kf-security-spring-boot-starter 项目设计文档

## 1. E-R 图

![kf-security-e-r](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/kf-security-e-r.png)

### 1.1 实体模型介绍

#### kf_security_dept

表示一个部门的信息，部门的相关属性如下：

|   属性名    |              描述              |
| :---------: | :----------------------------: |
|     id      |     主键，自增，无业务意义     |
|  dept_name  |             部门名             |
|  parent_id  |            父部门id            |
|    leaf     |        是否叶子节点部门        |
|    level    | 所属层级，parentId为0的层级为1 |
| description |              描述              |
|  app_name   |            应用名称            |
| create_time |            创建时间            |
| update_time |            更新时间            |
|  is_delete  |  是否删除 0：未删除 1：已删除  |

#### kf_security_message

表示一条消息，消息相关属性如下：

|   属性名    |              描述              |
| :---------: | :----------------------------: |
|     id      |     主键，自增，无业务意义     |
|    title    |              标题              |
|   content   |              内容              |
|  read_tag   |    是否已读 0：未读 1：已读    |
|  oplog_id   |           操作日志id           |
|   user_id   | 用户id，这条消息属于哪个用户的 |
|  app_name   |            应用名称            |
| create_time |            创建时间            |
| update_time |            更新时间            |
|  is_delete  |  是否删除 0：未删除 1：已删除  |

#### kf_security_oplog

表示一条操作日志，操作日志相关属性如下：

|      属性名       |             描述             |
| :---------------: | :--------------------------: |
|        id         |    主键，自增，无业务意义    |
|    operator_ip    |           操作者ip           |
|     operator      |          操作者账号          |
|   operate_page    |           操作页面           |
|   operate_type    |           操作类型           |
|    target_type    |           对象分类           |
|      target       |           操作对象           |
| operation_methods |           操作方式           |
|      detail       |         操作日志详情         |
|     app_name      |           应用名称           |
|    create_time    |           创建时间           |
|    update_time    |           更新时间           |
|     is_delete     | 是否删除 0：未删除 1：已删除 |

#### kf_security_oplog_extra

表示一条操作日志的扩展信息，操作日志扩展信息相关属性如下：

|   属性名    |                     描述                      |
| :---------: | :-------------------------------------------: |
|     id      |            主键，自增，无业务意义             |
|    info     |              操作日志的扩展信息               |
|    type     | 信息类型：1：操作页面 2：操作类型 3：对象分类 |
|  app_name   |                   应用名称                    |
| create_time |                   创建时间                    |
| update_time |                   更新时间                    |
|  is_delete  |         是否删除 0：未删除 1：已删除          |

#### kf_security_permission

表示一个权限点信息，权限点相关属性如下：

|     属性名      |                 描述                 |
| :-------------: | :----------------------------------: |
|       id        |        主键，自增，无业务意义        |
| permission_name |                权限名                |
|    parent_id    |               父权限id               |
|      leaf       |           是否叶节点权限点           |
|      level      | 权限点的层级（parentId为0的层级为1） |
|   description   |              权限点描述              |
|    app_name     |               应用名称               |
|   create_time   |               创建时间               |
|   update_time   |               更新时间               |
|    is_delete    |     是否删除 0：未删除 1：已删除     |

#### kf_security_project

表示一个项目信息，项目相关属性如下：

|    属性名    |             描述             |
| :----------: | :--------------------------: |
|      id      |    主键，自增，无业务意义    |
| project_code |           项目编号           |
| project_name |            项目名            |
| description  |           项目描述           |
|   dept_id    |            部门id            |
|   running    |  启用/停用 0：停用 1：启用   |
|   app_name   |           应用名称           |
| create_time  |           创建时间           |
| update_time  |           更新时间           |
|  is_delete   | 是否删除 0：未删除 1：已删除 |

#### kf_security_resource_type

表示一个资源类型，资源类型相关属性如下：

|   属性名    |             描述             |
| :---------: | :--------------------------: |
|     id      |    主键，自增，无业务意义    |
|  type_name  |          资源类型名          |
|  app_name   |           应用名称           |
| create_time |           创建时间           |
| update_time |           更新时间           |
|  is_delete  | 是否删除 0：未删除 1：已删除 |

#### kf_security_role

表示一个角色，角色相关属性如下：

|    属性名    |             描述             |
| :----------: | :--------------------------: |
|      id      |    主键，自增，无业务意义    |
|  role_code   |           角色编号           |
|  role_name   |            角色名            |
| description  |           角色描述           |
| last_reviser |          最后修改人          |
|   app_name   |           应用名称           |
| create_time  |           创建时间           |
| update_time  |           更新时间           |
|  is_delete   | 是否删除 0：未删除 1：已删除 |

#### kf_security_user

表示一个用户，用户相关属性如下：

|   属性名    |             描述             |
| :---------: | :--------------------------: |
|     id      |    主键，自增，无业务意义    |
|  user_name  |           用户账号           |
|     pw      |           用户密码           |
|    salt     |            密码盐            |
|  real_name  |           真实姓名           |
|    phone    |           手机号码           |
|    email    |           电子邮箱           |
|   dept_id   |          所属部门id          |
|  app_name   |           应用名称           |
| create_time |           创建时间           |
| update_time |           更新时间           |
|  is_delete  | 是否删除 0：未删除 1：已删除 |

#### kf_security_role_permission

表示一条`角色-权限`关联关系（仅保留叶节点权限与角色关系），`角色-权限`关联关系相关属性如下：

|    属性名     |             描述             |
| :-----------: | :--------------------------: |
|      id       |    主键，自增，无业务意义    |
|    role_id    |            角色id            |
| permission_id |            权限id            |
|   app_name    |           应用名称           |
|  create_time  |           创建时间           |
|  update_time  |           更新时间           |
|   is_delete   | 是否删除 0：未删除 1：已删除 |

#### kf_security_user_project

表示一条`用户-项目`关联关系，`用户-项目`关联关系相关属性如下：

|   属性名    |             描述             |
| :---------: | :--------------------------: |
|     id      |    主键，自增，无业务意义    |
|   user_id   |            用户id            |
| project_id  |            项目id            |
|  app_name   |           应用名称           |
| create_time |           创建时间           |
| update_time |           更新时间           |
|  is_delete  | 是否删除 0：未删除 1：已删除 |

#### kf_security_user_resource

表示一条`用户-资源-项目`关联关系，`用户-资源-项目`关联关系相关属性如下：

|      属性名      |               描述                |
| :--------------: | :-------------------------------: |
|        id        |      主键，自增，无业务意义       |
|     user_id      |              用户id               |
|    project_id    |              项目id               |
| resource_type_id |            资源类别id             |
|   resource_id    |              资源id               |
|  control_level   | 管理级别：1：查看权限 2：管理权限 |
|     app_name     |             应用名称              |
|   create_time    |             创建时间              |
|   update_time    |             更新时间              |
|    is_delete     |   是否删除 0：未删除 1：已删除    |

#### kf_security_user_role

表示一条`用户-角色`关联关系，`用户-角色`关联关系相关属性如下：

|   属性名    |             描述             |
| :---------: | :--------------------------: |
|     id      |    主键，自增，无业务意义    |
|   user_id   |            用户id            |
|   role_id   |            角色id            |
|  app_name   |           应用名称           |
| create_time |           创建时间           |
| update_time |           更新时间           |
|  is_delete  | 是否删除 0：未删除 1：已删除 |

#### kf_security_config

表示一个配置项，配置项相关属性如下：

|   属性名    |               描述               |
| :---------: | :------------------------------: |
|     id      |      主键，自增，无业务意义      |
| value_group |           配置项所属组           |
| value_name  |             配置项名             |
|    value    |             配置项值             |
|    edit     | 是否可编辑 1：不可编辑 2：可编辑 |
|   status    |    配置项状态 1：正常 2：禁用    |
|    memo     |               备注               |
|  operator   |              操作者              |
|  app_name   |             应用名称             |
| create_time |             创建时间             |
| update_time |             更新时间             |
|  is_delete  |   是否删除 0：未删除 1：已删除   |

## 2. 架构概述

​	kf-security-spring-boot-starter 是一个提供了`用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知`基础功能的项目，该项目基于 spring-boot 构建，三层架构：

- Controller：控制层，API 的实现，负责前后端交互，依赖 Service 层。接收客户端发送的 HTTP 请求，然后调用 Service 层相关接口，封装 Service 层返回的数据，并将其返回给客户端。相关类存放于包`com.didiglobal.knowframework.security.controller`下。
- Service：业务层，负责业务逻辑的处理，依赖 Dao 层。相关接口、类存放于包`com.didiglobal.knowframework.security.service`下。
- Dao：数据访问层，基于 mybatis，主要是读、写数据库，完成增、删、改、查功能，把数据返回给 Service 层。相关接口、类存放于包`com.didiglobal.knowframework.security.dao`下。
- Model：数据模型层，作为数据传输载体。相关类存放于包`com.didiglobal.knowframework.security.common.po`、`com.didiglobal.knowframework.security.common.entity`、`com.didiglobal.knowframework.security.common.vo`、`com.didiglobal.knowframework.security.common.dto`下。

### 2.1 业务流程的代码调用过程

​	这里以用户新增接口（PUT /logi-security/api/v1/user/add）为例，其调用时序图如下：

![UserController-add](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/UserController-add.png)

![UserService-addUser](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/UserService-addUser.png)

![UserRoleService-updateUserRoleByUserId](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/UserRoleService-updateUserRoleByUserId.png)

### 2.2 可扩展能力

​	在一些业务中，可能存在一些可变的流程，这些流程因不同的需求而不同，因而这需要业务具备可扩展能力，并尽可能提供给二开人员以较容易的集成方式。kf-security-spring-boot-starter 在`登录`、`资源`相关业务处理中具备可扩展能力，其扩展能力基于配置与 Spring Bean 管理容器`ApplicationContext`实现，这里以登录业务中的扩展接口为例：

#### 2.2.1 扩展加载器

​	在业务层 LoginServiceImpl 类中，中有一个 LoginExtendBeanTool 对象，LoginExtendBeanTool 是一个普通的 Spring Bean，用于获取登录业务的扩展接口实现（LoginExtendBeanTool 中的 getLoginExtendImpl 方法），其流程为根据用户配置的扩展 Bean 名，从 Spring 容器中获取，如未获取到，则获取默认实现，如下图：

![LoginServiceImpl](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/LoginServiceImpl.png)

![LoginExtendBeanTool](https://images-github.oss-cn-hangzhou.aliyuncs.com/know-framework/doc-design/LoginExtendBeanTool.png)

#### 2.2.2 扩展自定义实现逻辑

​	编写自定义的登录业务扩展接口实现类（实现 LoginExtend 接口），实现类须添加注解`@Component`使其被 Spring 容器接管，在配置文件中，添加配置项`login-extend-bean-name`，该配置项值为注解`@Component`中设置的 Bean Name。

