package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.OplogDto;
import com.didiglobal.logi.security.common.dto.ResourceDto;
import com.didiglobal.logi.security.common.entity.*;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.enums.resource.ShowLevelCode;
import com.didiglobal.logi.security.common.vo.resource.*;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.extend.OplogExtend;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.mapper.ProjectMapper;
import com.didiglobal.logi.security.mapper.ResourceTypeMapper;
import com.didiglobal.logi.security.mapper.UserMapper;
import com.didiglobal.logi.security.mapper.UserResourceMapper;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.service.ResourceService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceExtend resourceExtend;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private OplogExtend oplogExtend;

    @Override
    public List<ResourceTypeVo> getResourceTypeList() {

        List<ResourceType> resourceTypeList = resourceTypeMapper.selectList(null);
        return CopyBeanUtil.copyList(resourceTypeList, ResourceTypeVo.class);
    }

    @Override
    public List<MByUDataVo> getManagerByUserDataList(MByUDataQueryVo queryVo) {
        // 检查参数
        checkParam(queryVo);

        List<MByUDataVo> resultList = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();

        Integer projectId = queryVo.getProjectId();
        Integer resourceTypeId = queryVo.getResourceTypeId();
        Integer showLevel = queryVo.getShowLevel();
        int controlLevel = queryVo.getControlLevel();

        if(ShowLevelCode.PROJECT.getType().equals(showLevel)) {
            // 如果是项目展示级别
            QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
            List<Project> projectList = projectMapper.selectList(projectWrapper.select("id", "project_name"));
            for(Project project : projectList) {
                MByUDataVo dataVo = new MByUDataVo(project.getId(), project.getProjectName());
                if(queryVo.isBatch()) {
                    // 如果是批量操作，则默认假设都不拥有权限
                    dataVo.setHasLevel(0);
                } else {
                    wrapQueryCriteria(queryWrapper, showLevel, controlLevel, project.getId());
                    queryWrapper.eq("user_id", queryVo.getUserId());
                    int cnt = userResourceMapper.selectCount(queryWrapper);
                    if(cnt == 0) {
                        dataVo.setHasLevel(0);
                    } else {
                        // 获取该项目下具体资源的个数
                        int resourceCnt = resourceExtend.getResourceCnt(project.getId(), null);
                        dataVo.setHasLevel(cnt == resourceCnt ? 2 : 1);
                    }
                }
                resultList.add(dataVo);
                queryWrapper.clear();
            }
        } else if(ShowLevelCode.RESOURCE_TYPE.getType().equals(showLevel)) {
            // 如果是资源类别展示级别
            List<ResourceType> resourceTypeList = resourceTypeMapper.selectList(null);
            for(ResourceType resourceType : resourceTypeList) {
                MByUDataVo dataVo = new MByUDataVo(resourceType.getId(), resourceType.getTypeName());
                if(queryVo.isBatch()) {
                    // 如果是批量操作，则默认假设都不拥有权限
                    dataVo.setHasLevel(0);
                } else {
                    wrapQueryCriteria(queryWrapper, showLevel, controlLevel, projectId, resourceType.getId());
                    queryWrapper.eq("user_id", queryVo.getUserId());
                    int cnt = userResourceMapper.selectCount(queryWrapper);
                    if(cnt == 0) {
                        dataVo.setHasLevel(0);
                    } else {
                        // 获取该项目下具体资源的个数
                        int resourceCnt = resourceExtend.getResourceCnt(projectId, resourceType.getId());
                        dataVo.setHasLevel(cnt == resourceCnt ? 2 : 1);
                    }
                }
                resultList.add(dataVo);
                queryWrapper.clear();
            }
        } else {
            // 如果是具体资源展示级别
            List<ResourceDto> resourceDtoList = resourceExtend.getResourceList(projectId, resourceTypeId);
            for(ResourceDto resourceDto : resourceDtoList) {
                MByUDataVo dataVo = new MByUDataVo(resourceDto.getResourceId(), resourceDto.getResourceName());
                if(queryVo.isBatch()) {
                    // 如果是批量操作，则默认假设都不拥有权限
                    dataVo.setHasLevel(0);
                } else {
                    wrapQueryCriteria(queryWrapper, showLevel, controlLevel, projectId, resourceTypeId, resourceDto.getResourceId());
                    queryWrapper.eq("user_id", queryVo.getUserId());
                    int cnt = userResourceMapper.selectCount(queryWrapper);
                    dataVo.setHasLevel(cnt == 0 ? 0 : 2);
                }
                resultList.add(dataVo);
                queryWrapper.clear();
            }
        }
        return resultList;
    }

    @Override
    public List<MByRDataVo> getManagerByResourceDataList(MByRDataQueryVo queryVo) {
        checkParam(queryVo);
        Integer projectId = queryVo.getProjectId();
        Integer resourceTypeId = queryVo.getResourceTypeId();
        Integer resourceId = queryVo.getResourceId();
        Integer controlLevel = queryVo.getControlLevel();

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        // 封装获取用户条件，根据用户添加时间排序（倒序）
        userWrapper.select("id", "username", "real_name").orderByDesc("create_time")
                .like(!StringUtils.isEmpty(queryVo.getName()), "username", queryVo.getName())
                .or()
                .like(!StringUtils.isEmpty(queryVo.getName()), "real_name", queryVo.getName());
        List<User> userList = userMapper.selectList(userWrapper);

        List<MByRDataVo> list = new ArrayList<>();
        // 获取该项目下和某个资源类别下具体资源的个数
        int resourceCnt = 1;
        if(resourceId == null) {
            // 如果具体资源id为null，则获取某个项目下某个资源类别的具体资源个数
            resourceCnt = resourceExtend.getResourceCnt(projectId, resourceTypeId);
        }
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        for(User user : userList) {
            MByRDataVo dataVo = new MByRDataVo();
            dataVo.setId(user.getId());
            dataVo.setName(user.getUsername() + "/" + user.getRealName());
            if(queryVo.isBatch()) {
                // 如果是批量操作，则默认假设都不拥有权限
                dataVo.setHasLevel(0);
            } else {
                queryWrapper.clear();
                queryWrapper
                        .eq("user_id", user.getId())
                        .eq("project_id", projectId)
                        .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                        .eq(resourceId != null, "resource_id", resourceId)
                        .eq("control_level", controlLevel);
                int cnt = userResourceMapper.selectCount(queryWrapper);
                if(cnt == 0) {
                    dataVo.setHasLevel(0);
                } else {
                    dataVo.setHasLevel(cnt == resourceCnt ? 2 : 1);
                }
            }
            list.add(dataVo);
        }
        return list;
    }

    private void checkParam(Integer controlLevel, Integer projectId, Integer resourceTypeId, Integer resourceId) {
        if(projectId == null) {
            // 项目id不可为nul
            throw new SecurityException(ResultCode.PROJECT_ID_CANNOT_BE_NULL);
        }
        Project project = projectMapper.selectById(projectId);
        if(project == null) {
            // 项目不存在
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
        if(!project.getIsRunning()) {
            // 项目已停运
            throw new SecurityException(ResultCode.PROJECT_UN_RUNNING);
        }
        if(resourceTypeId == null && resourceId != null) {
            // 这种情况不允许出现（如果resourceId != null，则resourceTypeId必不为null）
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_ERROR);
        }
        if(ControlLevelCode.getByType(controlLevel) == null) {
            throw new SecurityException(ResultCode.RESOURCE_INVALID_CONTROL_LEVEL);
        }
    }

    private void checkParam(MByRDataQueryVo queryVo) {
        Integer projectId = queryVo.getProjectId();
        Integer resourceTypeId = queryVo.getResourceTypeId();
        Integer resourceId = queryVo.getResourceId();
        Integer controlLevel = queryVo.getControlLevel();
        checkParam(controlLevel, projectId, resourceTypeId, resourceId);
    }

    private void checkParam(MByUDataQueryVo queryVo) {
        if(queryVo.getUserId() == null) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        checkParam(queryVo.getShowLevel(), queryVo.getProjectId(), queryVo.getResourceTypeId());
    }

    private void checkParam(AssignToOneUserVo assignToOneUserVo) {
        Integer userId = assignToOneUserVo.getUserId();
        if(userId == null || userMapper.selectById(userId) == null) {
            throw new SecurityException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        if(ControlLevelCode.getByType(assignToOneUserVo.getControlLevel()) == null) {
            throw new SecurityException(ResultCode.RESOURCE_INVALID_CONTROL_LEVEL);
        }
        Integer projectId = assignToOneUserVo.getProjectId();
        Integer resourceTypeId = assignToOneUserVo.getResourceTypeId();
        if(projectId == null && resourceTypeId != null) {
            // 资源类别id不为null，则项目id不可为null
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_ERROR_2);
        }
    }

    private List<UserResource> getUserResourceList(Integer projectId, Integer resourceTypeId,
                                                   List<Integer> idList, List<Integer> userIdList, int controlLevel) {
        List<Object> projectIdList;
        List<Object> resourceTypeIdList;
        List<Object> resourceIdList = null;
        QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
        projectWrapper.select("id");
        QueryWrapper<ResourceType> resourceTypeWrapper = new QueryWrapper<>();
        resourceTypeWrapper.select("id");
        if(projectId == null) {
            // 说明idList是项目的idList
            projectIdList = new ArrayList<>(idList);
            resourceTypeIdList = resourceTypeMapper.selectObjs(resourceTypeWrapper);
        } else if(resourceTypeId == null) {
            // 说明idList是资源类别idList
            projectIdList = new ArrayList<>();
            projectIdList.add(projectId);
            resourceTypeIdList = new ArrayList<>(idList);
        } else {
            // 说明idList是具体资源idList
            projectIdList = new ArrayList<>();
            projectIdList.add(projectId);
            resourceTypeIdList = new ArrayList<>();
            resourceTypeIdList.add(resourceTypeId);
            resourceIdList = new ArrayList<>(idList);
        }
        // 封装List<ResourceDto>
        List<ResourceDto> resourceDtoList = new ArrayList<>();
        for(Object id1 : projectIdList) {
            for(Object id2 : resourceTypeIdList) {
                if(resourceIdList == null) {
                    resourceDtoList.addAll(resourceExtend.getResourceList((Integer) id1, (Integer) id2));
                } else {
                    for(Object id3 : resourceIdList) {
                        ResourceDto resourceDto = new ResourceDto();
                        resourceDto.setProjectId((Integer) id1);
                        resourceDto.setResourceTypeId((Integer) id2);
                        resourceDto.setResourceId((Integer) id3);
                        resourceDtoList.add(resourceDto);
                    }
                }
            }
        }
        // 封装List<UserResource>
        List<UserResource> userResourceList = new ArrayList<>();
        for(int userId : userIdList) {
            for(ResourceDto resourceDto : resourceDtoList) {
                UserResource userResource = new UserResource(resourceDto);
                userResource.setUserId(userId);
                userResource.setControlLevel(controlLevel);
                userResourceList.add(userResource);
            }
        }
        return userResourceList;
    }

    @Override
    public void assignResourcePermission(AssignToOneUserVo assignToOneUserVo) {
        // 检查参数
        checkParam(assignToOneUserVo);
        Integer userId = assignToOneUserVo.getUserId();
        Integer projectId = assignToOneUserVo.getProjectId();
        Integer resourceTypeId = assignToOneUserVo.getResourceTypeId();
        int controlLevel = assignToOneUserVo.getControlLevel();

        QueryWrapper<UserResource> userResourceWrapper = new QueryWrapper<>();
        userResourceWrapper
                .eq("user_id", userId)
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId);
        // 删除old的关联信息
        userResourceMapper.delete(userResourceWrapper);
        List<Integer> idList = assignToOneUserVo.getIdList();
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        List<UserResource> userResourceList = getUserResourceList(
                projectId, resourceTypeId, idList, userIdList, controlLevel
        );
        // 插入new关联信息
        userResourceMapper.insertBatchSomeColumn(userResourceList);

        // 保存操作日志 TODO：用户+资源名称 这个信息咋搞比较好，还要记录移除的信息
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("资源权限管理").operateType("分配资源")
                .targetType("用户").target("用户+资源名称").build()
        );
    }

    @Override
    public void assignResourcePermission(AssignToManyUserVo assignToManyUserVo) {
        // 检查参数
        checkParam(assignToManyUserVo);
        List<Integer> userIdList = assignToManyUserVo.getUserIdList();
        Integer projectId = assignToManyUserVo.getProjectId();
        Integer resourceTypeId = assignToManyUserVo.getResourceTypeId();
        Integer resourceId = assignToManyUserVo.getResourceId();

        QueryWrapper<UserResource> userResourceWrapper = new QueryWrapper<>();
        userResourceWrapper
                .eq( "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .eq(resourceId != null, "resource_id", resourceId);
        // 删除old关联信息
        userResourceMapper.delete(userResourceWrapper);

        List<ResourceDto> resourceDtoList = new ArrayList<>();
        if(resourceId == null) {
            // 说明是某个项目或者某个资源类别下的全部具体资源的权限分配给用户，获取所有的具体资源信息
            resourceDtoList.addAll(resourceExtend.getResourceList(projectId, resourceTypeId));
        } else {
            // 说明只有一个资源的权限分配给用户
            ResourceDto resourceDto = ResourceDto.builder()
                    .projectId(projectId).resourceTypeId(resourceTypeId).resourceId(resourceId)
                    .build();
            resourceDtoList.add(resourceDto);
        }
        // 插入new关联信息
        for(Integer userId : userIdList) {
            List<UserResource> userResourceList = new ArrayList<>();
            for(ResourceDto resourceDto : resourceDtoList) {
                UserResource userResource = new UserResource(resourceDto);
                userResource.setUserId(userId);
                userResource.setControlLevel(assignToManyUserVo.getControlLevel());
                userResourceList.add(userResource);
            }
            userResourceMapper.insertBatchSomeColumn(userResourceList);
        }

        // 保存操作日志 TODO：资源名称+用户 这个信息咋搞比较好？还要记录移除的信息
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("资源权限管理").operateType("分配用户")
                .targetType("资源").target("资源名称+用户").build()
        );
    }

    /**
     * 批量分配前删除全部old的关联信息
     * @param projectId 项目id
     * @param resourceTypeId 资源类别id
     * @param flag 批量分配用户 or 批量分配资源
     * @param controlLevel 资源权限控制级别
     * @param idList 用户idList、项目idList、资源类别idLIst、具体资源idList
     */
    private void deleteOldRelationBeforeBatchAssign(Integer projectId, Integer resourceTypeId,
                                                    boolean flag, int controlLevel, List<Integer> idList) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("control_level", controlLevel)
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId);
        if(flag) {
            // 按资源管理/批量分配用户，先删除N资源先前已分配的用户
            if(projectId == null) {
                // 项目批量分配级别
                queryWrapper.in("project_id", idList);
            } else if(resourceTypeId == null) {
                // 资源类别批量分配级别
                queryWrapper.in("resource_type_id", idList);
            } else {
                // 具体资源批量分配级别
                queryWrapper.in("resource_id", idList);
            }
        } else {
            // 按用户管理/批量分配资源，先删除N用户先前分配的资源权限
            queryWrapper.in("user_id", idList);
        }
        userResourceMapper.delete(queryWrapper);
    }

    @Override
    public void batchAssignResourcePermission(BatchAssignVo batchAssignVo) {
        // 检查参数
        checkParam(batchAssignVo);
        // 获取参数
        Integer projectId = batchAssignVo.getProjectId();
        Integer resourceTypeId = batchAssignVo.getResourceTypeId();
        List<Integer> userIdList = batchAssignVo.getUserIdList();
        List<Integer> idList = batchAssignVo.getIdList();
        int controlLevel = batchAssignVo.getControlLevel();
        boolean assignFlag = batchAssignVo.getAssignFlag();
        // 先删除全部old关联信息
        deleteOldRelationBeforeBatchAssign(
                projectId, resourceTypeId, assignFlag, controlLevel, idList
        );
        // 获取新管理信息
        List<UserResource> userResourceList = getUserResourceList(
                projectId, resourceTypeId, idList, userIdList, controlLevel
        );
        // 插入新关联信息
        userResourceMapper.insertBatchSomeColumn(userResourceList);

        if(assignFlag) {
            // 保存操作日志 TODO：资源名称+用户 这个信息咋搞比较好？还要记录移除的信息
            oplogExtend.saveOplog(OplogDto.builder()
                    .operatePage("资源权限管理").operateType("批量分配用户")
                    .targetType("资源").target("资源名称+用户").build()
            );
        } else {
            // 保存操作日志 TODO：用户+资源名称 这个信息咋搞比较好？还要记录移除的信息
            oplogExtend.saveOplog(OplogDto.builder()
                    .operatePage("资源权限管理").operateType("批量分配资源")
                    .targetType("用户").target("用户+资源名称").build()
            );
        }
    }

    private void checkParam(BatchAssignVo batchAssignVo) {
        if(batchAssignVo.getAssignFlag() == null) {
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_BATCH_FLAG_CANNOT_BE_NULL);
        }
        if(CollectionUtils.isEmpty(batchAssignVo.getUserIdList())) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        Integer projectId = batchAssignVo.getProjectId();
        Integer resourceTypeId = batchAssignVo.getResourceTypeId();
        if(projectId == null && resourceTypeId != null) {
            // 资源类别id不为null，则项目id不可为null
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_ERROR_2);
        }
        if(ControlLevelCode.getByType(batchAssignVo.getControlLevel()) == null) {
            throw new SecurityException(ResultCode.RESOURCE_INVALID_CONTROL_LEVEL);
        }
    }

    private void checkParam(AssignToManyUserVo assignToManyUserVo) {
        Integer projectId = assignToManyUserVo.getProjectId();
        Integer resourceTypeId = assignToManyUserVo.getResourceTypeId();
        Integer resourceId = assignToManyUserVo.getResourceId();
        Integer controlLevel = assignToManyUserVo.getControlLevel();
        checkParam(controlLevel, projectId, resourceTypeId, resourceId);
    }

    //--------------------------资源权限管理（按用户管理）begin--------------------------

    @Override
    public PagingData<MByUVo> getManageByUserPage(MByUQueryVo queryVo) {
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        IPage<User> userPage = new Page<>(queryVo.getPage(), queryVo.getSize());

        // 拼接查询条件
        List<Integer> deptIdList = deptService.getChildDeptIdListByParentId(queryVo.getDeptId());
        userWrapper
                .like(queryVo.getUsername() != null, "username", queryVo.getUsername())
                .like(queryVo.getRealName() != null, "real_name", queryVo.getRealName())
                .in(queryVo.getDeptId() != null, "dept_id", deptIdList);
        userMapper.selectPage(userPage, userWrapper);

        List<MByUVo> list = new ArrayList<>();

        QueryWrapper<UserResource> userResourceWrapper = new QueryWrapper<>();
        for(User user : userPage.getRecords()) {
            MByUVo dataVo = CopyBeanUtil.copy(user, MByUVo.class);
            dataVo.setUserId(user.getId());
            // 设置部门信息
            dataVo.setDeptInfo(deptService.spliceDeptInfo(user.getDeptId()));
            // 计算管理权限资源数
            userResourceWrapper
                    .eq("user_id", user.getId())
                    .eq("control_level", ControlLevelCode.ADMIN.getType());
            dataVo.setAdminResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
            userResourceWrapper.clear();

            // 计算查看权限资源数
            userResourceWrapper
                    .eq("user_id", user.getId())
                    .eq("control_level", ControlLevelCode.VIEW.getType());
            dataVo.setViewResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
            userResourceWrapper.clear();

            list.add(dataVo);
        }
        return new PagingData<>(list, userPage);
    }

    //--------------------------资源权限管理（按用户管理）end--------------------------

    //--------------------------资源权限管理（按资源管理）begin--------------------------

    @Override
    public PagingData<MByRVo> getManageByResourcePage(MByRQueryVo queryVo) {
        // 检查参数
        checkParam(queryVo);
        if(queryVo.getShowLevel().equals(ShowLevelCode.PROJECT.getType())) {
            // 项目展示级别，表示查找所有项目
            return dealProjectLevel(queryVo, queryVo.getShowLevel());
        } else if(queryVo.getShowLevel().equals(ShowLevelCode.RESOURCE_TYPE.getType())) {
            // 资源类别展示级别，表示查找某个项目下所有资源类别
            return dealResourceTypeLevel(queryVo, queryVo.getShowLevel());
        } else {
            // 具体资源展示级别，表示查找该项目下该资源类别对应的资源
            return dealResourceLevel(queryVo, queryVo.getShowLevel());
        }
    }

    private void checkParam(Integer showLevel, Integer projectId, Integer resourceTypeId) {
        if(ShowLevelCode.getByType(showLevel) == null) {
            // 请输入有效的展示级别（1 <= showLevel <= 3）
            throw new SecurityException(ResultCode.RESOURCE_INVALID_SHOW_LEVEL);
        }
        if(showLevel.equals(ShowLevelCode.RESOURCE_TYPE.getType())) {
            // 资源类别展示级别，表示查找某个项目下所有资源类别
            if(projectId == null) {
                // 2级展示级别，项目id不可为null
                throw new SecurityException(ResultCode.RESOURCE_SHOW_LEVEL_ERROR);
            }
        } else if(showLevel.equals(ShowLevelCode.RESOURCE.getType())){
            // 具体资源展示级别，表示查找该项目下该资源类别对应的资源
            if(projectId == null || resourceTypeId == null) {
                // 3级展示级别，项目id或资源类别id不可为null
                throw new SecurityException(ResultCode.RESOURCE_SHOW_LEVEL_ERROR_2);
            }
        }
    }

    private void checkParam(MByRQueryVo queryVo) {
        checkParam(queryVo.getShowLevel(), queryVo.getProjectId(), queryVo.getResourceTypeId());
    }

    /**
     * 资源权限管理>按资源管理的列表信息
     * 封装PagingData
     * @param list 数据
     * @param pagination 分页信息
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVo> buildPagingData(List<MByRVo> list, PagingData.Pagination pagination) {
        PagingData<MByRVo> pagingData = new PagingData<>();
        pagingData.setPagination(pagination);
        pagingData.setBizData(list);
        return pagingData;
    }

    /**
     * 资源权限管理>按资源管理的列表信息
     * 根据级别拼装【管理权限用户数 或 管理权限用户数】的查询条件
     * @param queryWrapper 数据库查询条件
     * @param showLevel 按资源管理展示级别
     * @param controlLevel 资源权限级别
     * @param ids 各种id的集合，顺序为：project_id、resource_type_id、resource_id
     */
    private void wrapQueryCriteria(QueryWrapper<UserResource> queryWrapper, int showLevel,
                                 int controlLevel, Object ... ids) {
        queryWrapper.clear();
        // 拼接管理权限或查看权限条件
        queryWrapper.eq("control_level", controlLevel);

        // 根据展示级别拼接条件
        if(showLevel >= ShowLevelCode.PROJECT.getType()) {
            // 具体项目级别
            queryWrapper.eq("project_id", ids[0]);
        }
        if(showLevel >= ShowLevelCode.RESOURCE_TYPE.getType()) {
            // 具体资源类别级别
            queryWrapper.eq("resource_type_id", ids[1]);
        }
        if(showLevel >= ShowLevelCode.RESOURCE.getType()) {
            // 具体资源级别
            queryWrapper.eq("resource_id", ids[2]);
        }
    }

    /**
     * 资源权限管理>按资源管理的列表信息>项目级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVo> dealProjectLevel(MByRQueryVo queryVo, int showLevel) {
        List<MByRVo> list = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();

        IPage<Project> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(queryVo.getName())) {
            // 如果有名字查询条件
            projectWrapper.like("project_name", queryVo.getName());
        }
        projectMapper.selectPage(iPage, projectWrapper);

        for(Project project : iPage.getRecords()) {
            MByRVo data = new MByRVo();
            data.otherAttribute().put("projectId", project.getId());
            data.otherAttribute().put("projectCode", project.getProjectCode());
            data.otherAttribute().put("projectName", project.getProjectName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN.getType(), project.getId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW.getType(), project.getId());
            data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(data);
        }
        return new PagingData<>(list, iPage);
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源列别级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVo> dealResourceTypeLevel(MByRQueryVo queryVo, int showLevel) {
        List<MByRVo> list = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();

        IPage<ResourceType> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<ResourceType> resourceTypeWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(queryVo.getName())) {
            // 如果有名字查询条件
            resourceTypeWrapper.like("type_name", queryVo.getName());
        }
        resourceTypeMapper.selectPage(iPage, resourceTypeWrapper);

        // 获取项目信息
        Project project = projectMapper.selectById(queryVo.getProjectId());
        for(ResourceType resourceType : iPage.getRecords()) {
            MByRVo data = new MByRVo();
            data.otherAttribute().put("resourceTypeId", resourceType.getId());
            data.otherAttribute().put("projectId", queryVo.getProjectId());
            data.otherAttribute().put("resourceTypeName", resourceType.getTypeName());
            data.otherAttribute().put("projectName", project.getProjectName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN.getType(),
                    project.getId(), resourceType.getId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW.getType(),
                    project.getId(), resourceType.getId());
            data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(data);
        }
        return new PagingData<>(list, iPage);
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVo> dealResourceLevel(MByRQueryVo queryVo, int showLevel) {
        // 调用扩展接口获取具体资源信息
        PagingData<ResourceDto> page = resourceExtend.getResourcePage(
                queryVo.getProjectId(),
                queryVo.getResourceTypeId(),
                queryVo.getName(),
                queryVo.getPage(),
                queryVo.getSize()
        );
        List<MByRVo> list = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        // 获取资源类别信息
        ResourceType resourceType = resourceTypeMapper.selectById(queryVo.getResourceTypeId());

        for(ResourceDto resourceDto : page.getBizData()) {
            MByRVo data = new MByRVo();

            data.otherAttribute().put("resourceId", resourceDto.getResourceId());
            data.otherAttribute().put("resourceTypeId", queryVo.getResourceTypeId());
            data.otherAttribute().put("projectId", queryVo.getProjectId());
            data.otherAttribute().put("resourceName", resourceDto.getResourceName());
            data.otherAttribute().put("resourceTypeName", resourceType.getTypeName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN.getType(),
                    queryVo.getProjectId(), resourceType.getId(), resourceDto.getResourceId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW.getType(),
                    queryVo.getProjectId(), resourceType.getId(), resourceDto.getResourceId());
            data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(data);
        }
        return buildPagingData(list, page.getPagination());
    }

    //--------------------------资源权限管理（按资源管理）end--------------------------
}
