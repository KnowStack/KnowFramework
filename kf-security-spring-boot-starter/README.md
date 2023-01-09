# kf-security-spring-boot-starter

### 介绍
kf-security 提供项目大多都需要基础的一些功能（用户、角色、权限、登录、注册、操作记录），这些功能开发简单但是开发起来又比较繁琐和有一定的工作量（开发、测试、联调、编写接口文档等等），所以打算把这类的功能抽取出来，整合进 kf-security，让这些项目开发人员更加专注于核心功能，避免时间花费在繁琐的基础功能的开发。
### 功能支持
主要提供：用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知
- 用户模块：提供了注册、登录、认证功能，以及用户信息的展示等基础功能；
- 项目模块：提供了创建、删除、展示、更改运行状态等功能；
- 部门模块：提供了部门信息导入、部门树形结构展示等功能；
- 界面权限模块：提供界面权限信息的导入、界面功能展示控制等功能；
- 资源权限模块：提供对项目具体资源的权限管理；
- 操作日志模块：记录用户的操作记录，展示记录；
- 消息通知模块：用户拥有的角色、拥有的资源权限变更等通知。
### 使用方式
#### 1.添加Maven
```xml
<dependency>
    <groupId>io.github.knowstack</groupId>
    <artifactId>kf-security-spring-boot-starter</artifactId>
    <version>1.0.2</version>
</dependency>
```
#### 2.创建数据库表
需要在mysql中执行 [kf-security.sql](./src/main/resources/kf-security.sql) ,以便初始化应用所需的数据库表结构；
#### 3.配置文件
kf-security基于springBoot框架开发，需添加以下配置信息：
```properties
kf.security.app-name: knowsearch
kf.security.resource-extend-bean-name: myResourceExtendImpl

# ---------------数据源信息---------------
kf.security.username=root
kf.security.password=123456
kf.security.url=jdbc:mysql://localhost:3306/kf_security?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
kf.security.driver-class-name=com.mysql.jdbc.Driver
```
- app-name：表示应用的名称，涉及到 kf-security 的数据库操作都会带上这个条件（查询、保存等操作）；
- resource-extend-bean-name：[ResourceExtend](./src/main/java/com/didiglobal/knowframework/security/extend/ResourceExtend.java) 的实现类在spring容器bean的名称，kf-security 中资源权限管理模块，需要获取具体资源的信息，所以用户需实现 ResourceExtend 接口并指定实现类在spring容器中bean的名称；
#### 3 需要实现接口
用户可选的实现 [ResourceExtend](./src/main/java/com/didiglobal/knowframework/security/extend/ResourceExtend.java) 接口，如果不实现，则资源权限管理模块的功能不能完整使用。接口详情见代码
#### 4 导入数据
kf-security相关界面并没提供【角色权限元数据、资源类别数据、部门信息数据、操作日志相关（操作页面、操作对象、对象分类）】的创建功能，kf-security提供了数据导入的接口。
建议全部都导入，简单的数据也行。

### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request