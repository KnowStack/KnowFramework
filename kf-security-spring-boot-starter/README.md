# kf-security-spring-boot-starter
    kf-security提供了PaaS平台基础的一些功能（用户、角色、权限、登录、注册、操作记录等），这些功能需要在平台建设初期就开发接入，虽然开发简单但是开发起来又比较繁琐且有一定的工作量（开发、测试、联调、编写接口文档等等），所以我们将这类功能抽取出来，整合到kf-security中，让这些项目的开发人员更加专注于核心功能，避免时间花费在繁琐的基础功能开发上。
## 功能支持
主要提供：用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知
- 用户模块：提供了注册、登录、认证功能，以及用户信息的展示等基础功能；
- 项目模块：提供了创建、删除、展示、更改运行状态等功能；
- 部门模块：提供了部门信息导入、部门树形结构展示等功能；
- 界面权限模块：提供界面权限信息的导入、界面功能展示控制等功能；
- 资源权限模块：提供对项目具体资源的权限管理；
- 操作日志模块：记录用户的操作记录，展示记录；
- 消息通知模块：用户拥有的角色、拥有的资源权限变更等通知。
## 设计概要
### ER图
![kf-security-ER图.png](https://note.youdao.com/yws/res/2065/WEBRESOURCE8ff999c533ed9517238af9609eb481c7)
### 表结构
![kf_security_表结构.png](https://note.youdao.com/yws/res/2062/WEBRESOURCEb84c30888df0a44232736de124c2362a)
### 接口文档
[kf-security接口文档](./kd-security-api-docs.md)

## 接入方式
### 1.添加Maven
```xml
<dependency>
    <groupId>io.github.knowstack</groupId>
    <artifactId>kf-security-spring-boot-starter</artifactId>
    <version>1.0.2</version>
</dependency>
```
### 2.创建数据库表
需要在mysql中执行 [kf-security.sql](./src/main/resources/kf-security.sql) ,以便初始化应用所需的数据库表结构；
### 3.配置文件
kf-security基于springBoot框架开发，需添加以下配置信息：
```properties
# 应用名称
kf.security.app-name: knowsearch
# 自定义实现ResourceExtend的bean名称
kf.security.resource-extend-bean-name: myResourceExtendImpl

# ---------------数据源信息---------------
kf.security.username=root
kf.security.password=123456
kf.security.url=jdbc:mysql://localhost:3306/kf_security?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
kf.security.driver-class-name=com.mysql.jdbc.Driver
```
## 注意与拓展
- 配置文件中的app-name：表示应用的名称，涉及到 kf-security 的数据库操作都会带上这个条件（查询、保存等操作），以便能够在同一个环境中使用多个应用平台的基础功能而不互相影响。
- 使用kf-security完整的资源权限管理时，需要自定义实现 ResourceExtend，并将其加载到spring容器中。如果不实现，则资源权限管理模块的功能不能完整使用。详情见代码：
```java
package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.po.ProjectResourcePO;
import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.dto.resource.ResourceDTO;
import com.didiglobal.knowframework.security.extend.ResourceExtend;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.mapper.ProjectResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component("myResourceExtendImpl")
public class ResourceExtendImpl implements ResourceExtend {

    @Autowired
    private ProjectResourceMapper projectResourceMapper;

    private static final String PROJECT_ID = "project_id";
    private static final String RESOURCE_TYPE_ID = "resource_type_id";
    private static final String RESOURCE_NAME = "resource_name";

    @Override
    public PagingData<ResourceDTO> getResourcePage(Integer projectId, Integer resourceTypeId,
                                                   String resourceName, int page, int size) {

        IPage<ProjectResourcePO> pageInfo = new Page<>(page, size);
        QueryWrapper<ProjectResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, PROJECT_ID, projectId)
                .eq(resourceTypeId != null, RESOURCE_TYPE_ID, resourceTypeId)
                .like(!StringUtils.isEmpty(resourceName), RESOURCE_NAME, resourceName);
        projectResourceMapper.selectPage(pageInfo, queryWrapper);
        List<ResourceDTO> resourceDTOList = CopyBeanUtil.copyList(pageInfo.getRecords(), ResourceDTO.class);
        return new PagingData<>(resourceDTOList, pageInfo);
    }

    @Override
    public List<ResourceDTO> getResourceList(Integer projectId, Integer resourceTypeId) {
        QueryWrapper<ProjectResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, PROJECT_ID, projectId)
                .eq(resourceTypeId != null, RESOURCE_TYPE_ID, resourceTypeId);
        List<ProjectResourcePO> projectResourcePOList = projectResourceMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(projectResourcePOList, ResourceDTO.class);
    }

    @Override
    public int getResourceCnt(Integer projectId, Integer resourceTypeId) {
        QueryWrapper<ProjectResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, PROJECT_ID, projectId)
                .eq(resourceTypeId != null, RESOURCE_TYPE_ID, resourceTypeId);
        return projectResourceMapper.selectCount(queryWrapper);
    }
}
```