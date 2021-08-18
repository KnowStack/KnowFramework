package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.resource.*;
import com.didiglobal.logi.security.common.dto2.OplogDto;
import com.didiglobal.logi.security.common.dto2.ResourceDto;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.enums.resource.HasLevelCode;
import com.didiglobal.logi.security.common.enums.resource.ShowLevelCode;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.common.po.ResourceTypePO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.po.UserResourcePO;
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
    public List<ResourceTypeVO> getResourceTypeList() {
        List<ResourceTypePO> resourceTypeList = resourceTypeMapper.selectList(null);
        return CopyBeanUtil.copyList(resourceTypeList, ResourceTypeVO.class);
    }

    @Override
    public List<MByUDataVO> getManagerByUserDataList(MByUDataQueryDTO queryVo) {
        // 检查参数
        checkParam(queryVo);

        Integer projectId = queryVo.getProjectId();
        Integer resourceTypeId = queryVo.getResourceTypeId();
        int showLevel = queryVo.getShowLevel();
        int controlLevel = queryVo.getControlLevel();
        int userId = queryVo.getUserId();
        boolean isBatch = queryVo.getBatch();

        List<MByUDataVO> resultList = new ArrayList<>();
        if(ShowLevelCode.PROJECT.getType().equals(showLevel)) {
            // 如果是项目展示级别
            QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
            List<ProjectPO> projectPOList = projectMapper.selectList(projectWrapper.select("id", "project_name"));
            for(ProjectPO projectPO : projectPOList) {
                MByUDataVO dataVo = new MByUDataVO(projectPO.getId(), projectPO.getProjectName());
                dataVo.setHasLevel(getHasLevel(
                        isBatch, showLevel, controlLevel, userId, projectPO.getId(), null, null
                ).getType());
                resultList.add(dataVo);
            }
        } else if(ShowLevelCode.RESOURCE_TYPE.getType().equals(showLevel)) {
            // 如果是资源类别展示级别
            List<ResourceTypePO> resourceTypeList = resourceTypeMapper.selectList(null);
            for(ResourceTypePO resourceTypePO : resourceTypeList) {
                MByUDataVO dataVo = new MByUDataVO(resourceTypePO.getId(), resourceTypePO.getTypeName());
                dataVo.setHasLevel(getHasLevel(
                        isBatch, showLevel, controlLevel, userId, projectId, resourceTypePO.getId(), null
                ).getType());
                resultList.add(dataVo);
            }
        } else {
            // 如果是具体资源展示级别
            List<ResourceDto> resourceDtoList = resourceExtend.getResourceList(projectId, resourceTypeId);
            for(ResourceDto resourceDto : resourceDtoList) {
                MByUDataVO dataVo = new MByUDataVO(resourceDto.getResourceId(), resourceDto.getResourceName());
                dataVo.setHasLevel(getHasLevel(
                        isBatch, showLevel, controlLevel, userId, projectId, resourceTypeId, resourceDto.getResourceId()
                ).getType());
                resultList.add(dataVo);
            }
        }
        return resultList;
    }

    @Override
    public List<MByRDataVO> getManagerByResourceDataList(MByRDataQueryDTO queryVo) {
        checkParam(queryVo);
        Integer projectId = queryVo.getProjectId();
        Integer resourceTypeId = queryVo.getResourceTypeId();
        Integer resourceId = queryVo.getResourceId();
        int controlLevel = queryVo.getControlLevel();
        boolean isBatch = queryVo.getBatch();

        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        // 封装获取用户条件，根据用户添加时间排序（倒序）
        userWrapper.select("id", "username", "real_name").orderByDesc("create_time")
                .like(!StringUtils.isEmpty(queryVo.getName()), "username", queryVo.getName())
                .or()
                .like(!StringUtils.isEmpty(queryVo.getName()), "real_name", queryVo.getName());
        List<UserPO> userList = userMapper.selectList(userWrapper);

        List<MByRDataVO> list = new ArrayList<>();
        for(UserPO userPO : userList) {
            MByRDataVO dataVo = new MByRDataVO();
            dataVo.setUserId(userPO.getId());
            dataVo.setUsername(userPO.getUsername());
            dataVo.setRealName(userPO.getRealName());
            dataVo.setHasLevel(getHasLevel(
                    isBatch, ShowLevelCode.RESOURCE.getType(), controlLevel, userPO.getId(), projectId, resourceTypeId, resourceId
            ).getType());
            list.add(dataVo);
        }
        return list;
    }

    /**
     * 获取用户userId，对该数据（项目、资源类别、具体资源）的拥有级别
     * @param showLevel 展示级别
     * @param controlLevel 资源控制级别
     * @param userId 用户id
     * @param projectId 项目id
     * @param resourceTypeId 资源类别id
     * @param resourceId 具体资源id
     * @return HasLevelCode 拥有级别枚举
     */
    private HasLevelCode getHasLevel(boolean isBatch, int showLevel, int controlLevel, int userId,
                                     Integer projectId, Integer resourceTypeId, Integer resourceId) {

        if(isBatch) {
            // 如果是批量操作，则默认假设都不拥有权限
            return HasLevelCode.NONE;
        }

        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        wrapQueryCriteria(queryWrapper, showLevel, controlLevel, projectId, resourceTypeId, resourceId);
        queryWrapper.eq("user_id", userId);
        int cnt = userResourceMapper.selectCount(queryWrapper);
        if(cnt == 0) {
            return HasLevelCode.NONE;
        } else {
            // 获取该项目下具体资源的个数
            int resourceCnt = 1;
            if(resourceId == null) {
                // 如果具体资源id为null，则获取某个项目下 || 某个项目下某个资源类别的具体资源个数
                resourceCnt = resourceExtend.getResourceCnt(projectId, resourceTypeId);
                return cnt == resourceCnt ? HasLevelCode.ALL : HasLevelCode.HALF;
            }
            // resourceId != null，则cnt最大为1
            return cnt == resourceCnt ? HasLevelCode.ALL : HasLevelCode.NONE;
        }
    }

    /**
     * VPC（ViewPermissionControl）
     * 判断资源查看权限控制状态是否开启
     * 只要数据库中有这样的记录（user_id、project_id、resource_type_id、resource_id、control_level）都为0
     * 则开启了
     */
    private QueryWrapper<UserResourcePO> buildVPCStatusQueryWrapper() {
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        return queryWrapper
                .eq("user_id", 0)
                .eq("project_id", 0)
                .eq("resource_type_id", 0)
                .eq("resource_id", 0)
                .eq("control_level", 0);
    }

    @Override
    public boolean getViewPermissionControlStatus() {
        QueryWrapper<UserResourcePO> queryWrapper = buildVPCStatusQueryWrapper();
        // 判断是否有记录
        return userResourceMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void changeResourceViewControlStatus() {
        // 先获取当前 资源查看权限控制状态
        boolean isOn = getViewPermissionControlStatus();

        if(isOn) {
            // 如果true，则之前已经打开了资源的查看权限控制，将要置为false，则所有人具有查看权限
            QueryWrapper<UserResourcePO> queryWrapper = buildVPCStatusQueryWrapper();
            // 删除该记录
            userResourceMapper.delete(queryWrapper);
        } else {
            // 如果false，则之前关闭了资源的查看权限控制，将要置为true，则所有人不具有查看权限
            QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
            // 则等于查看权限level的记录都要被删除（因为查看权限控制将被置为false，所有人默认具有查看权限）
            queryWrapper.eq("control_level", ControlLevelCode.VIEW.getType());
            userResourceMapper.delete(queryWrapper);
            // 构造全0的数据，表示 资源查看权限控制 被开启了
            UserResourcePO UserResourcePO = new UserResourcePO(0, 0, 0, 0, 0);
            userResourceMapper.insert(UserResourcePO);
        }
    }

    /**
     * 校验参数
     * @param controlLevel 控制级别
     * @param projectId 项目id
     * @param resourceTypeId 资源类别id
     * @param resourceId 具体资源id
     */
    private void checkParam(Integer controlLevel, Integer projectId, Integer resourceTypeId, Integer resourceId) {
        if(projectId == null) {
            // 项目id不可为nul
            throw new SecurityException(ResultCode.PROJECT_ID_CANNOT_BE_NULL);
        }
        ProjectPO projectPO = projectMapper.selectById(projectId);
        if(projectPO == null) {
            // 项目不存在
            throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
        }
        if(!projectPO.getRunning()) {
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

    private void checkParam(MByRDataQueryDTO queryVo) {
        checkParam(
                queryVo.getControlLevel(), queryVo.getProjectId(),
                queryVo.getResourceTypeId(), queryVo.getResourceId()
        );
    }

    private void checkParam(MByUDataQueryDTO queryVo) {
        if(queryVo.getUserId() == null) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        if(ControlLevelCode.getByType(queryVo.getControlLevel()) == null) {
            throw new SecurityException(ResultCode.RESOURCE_INVALID_CONTROL_LEVEL);
        }
        checkParam(queryVo.getShowLevel(), queryVo.getProjectId(), queryVo.getResourceTypeId());
    }

    private void checkParam(AssignToOneUserDTO assignToOneUserDTO) {
        Integer userId = assignToOneUserDTO.getUserId();
        if(userId == null || userMapper.selectById(userId) == null) {
            throw new SecurityException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        if(ControlLevelCode.getByType(assignToOneUserDTO.getControlLevel()) == null) {
            throw new SecurityException(ResultCode.RESOURCE_INVALID_CONTROL_LEVEL);
        }
        Integer projectId = assignToOneUserDTO.getProjectId();
        Integer resourceTypeId = assignToOneUserDTO.getResourceTypeId();
        if(projectId == null && resourceTypeId != null) {
            // 资源类别id不为null，则项目id不可为null
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_ERROR_2);
        }
    }

    /**
     * 封装UserResourceList，便于批量插入数据库
     * @param projectId 项目id
     * @param resourceTypeId 资源类型id
     * @param idList projectId==null，idList为项目idList、resourceTypeId==null，idList为资源类别idList
     * @param controlLevel 资源控制权限level
     * @param userIdList 用户idList
     * @return List<UserResourcePO>
     */
    private List<UserResourcePO> getUserResourceList(Integer projectId, Integer resourceTypeId, int controlLevel,
                                                   List<Integer> idList, List<Integer> userIdList) {
        List<Object> projectIdList;
        List<Object> resourceTypeIdList;
        List<Object> resourceIdList = null;
        QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
        projectWrapper.select("id");
        QueryWrapper<ResourceTypePO> resourceTypeWrapper = new QueryWrapper<>();
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
        // 封装List<UserResourcePO>
        List<UserResourcePO> userResourceList = new ArrayList<>();
        for(int userId : userIdList) {
            for(ResourceDto resourceDto : resourceDtoList) {
                UserResourcePO UserResourcePO = new UserResourcePO(resourceDto);
                UserResourcePO.setUserId(userId);
                UserResourcePO.setControlLevel(controlLevel);
                userResourceList.add(UserResourcePO);
            }
        }
        return userResourceList;
    }

    @Override
    public void assignResourcePermission(AssignToOneUserDTO assignToOneUserDTO) {
        // 检查参数
        checkParam(assignToOneUserDTO);
        Integer userId = assignToOneUserDTO.getUserId();
        Integer projectId = assignToOneUserDTO.getProjectId();
        Integer resourceTypeId = assignToOneUserDTO.getResourceTypeId();
        int controlLevel = assignToOneUserDTO.getControlLevel();

        QueryWrapper<UserResourcePO> userResourceWrapper = new QueryWrapper<>();
        userResourceWrapper
                .eq("user_id", userId)
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .eq("control_level", controlLevel);
        // 删除old的关联信息
        userResourceMapper.delete(userResourceWrapper);
        List<Integer> idList = assignToOneUserDTO.getIdList();
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        List<UserResourcePO> userResourceList = getUserResourceList(
                projectId, resourceTypeId, controlLevel, idList, userIdList
        );
        // 插入new关联信息
        if(!CollectionUtils.isEmpty(userResourceList)) {
            userResourceMapper.insertBatchSomeColumn(userResourceList);
        }

        // 保存操作日志 TODO：用户+资源名称 这个信息咋搞比较好，还要记录移除的信息
        oplogExtend.saveOplog(OplogDto.builder()
                .operatePage("资源权限管理").operateType("分配资源")
                .targetType("用户").target("用户+资源名称").build()
        );
    }

    @Override
    public void assignResourcePermission(AssignToManyUserDTO assignToManyUserDTO) {
        // 检查参数
        checkParam(assignToManyUserDTO);
        List<Integer> userIdList = assignToManyUserDTO.getUserIdList();
        Integer projectId = assignToManyUserDTO.getProjectId();
        Integer resourceTypeId = assignToManyUserDTO.getResourceTypeId();
        Integer resourceId = assignToManyUserDTO.getResourceId();

        QueryWrapper<UserResourcePO> userResourceWrapper = new QueryWrapper<>();
        userResourceWrapper
                .eq( "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .eq(resourceId != null, "resource_id", resourceId)
                .eq("control_level", assignToManyUserDTO.getControlLevel());
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
            List<UserResourcePO> userResourceList = new ArrayList<>();
            for(ResourceDto resourceDto : resourceDtoList) {
                UserResourcePO UserResourcePO = new UserResourcePO(resourceDto);
                UserResourcePO.setUserId(userId);
                UserResourcePO.setControlLevel(assignToManyUserDTO.getControlLevel());
                userResourceList.add(UserResourcePO);
            }
            if(!CollectionUtils.isEmpty(userResourceList)) {
                userResourceMapper.insertBatchSomeColumn(userResourceList);
            }
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
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .eq("control_level", controlLevel);
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
    public void batchAssignResourcePermission(BatchAssignDTO batchAssignDTO) {
        // 检查参数
        checkParam(batchAssignDTO);
        // 获取参数
        Integer projectId = batchAssignDTO.getProjectId();
        Integer resourceTypeId = batchAssignDTO.getResourceTypeId();
        List<Integer> userIdList = batchAssignDTO.getUserIdList();
        List<Integer> idList = batchAssignDTO.getIdList();
        int controlLevel = batchAssignDTO.getControlLevel();
        boolean assignFlag = batchAssignDTO.getAssignFlag();
        // 先删除全部old关联信息
        deleteOldRelationBeforeBatchAssign(
                projectId, resourceTypeId, assignFlag, controlLevel, idList
        );
        // 获取新管理信息
        List<UserResourcePO> userResourceList = getUserResourceList(
                projectId, resourceTypeId, controlLevel, idList, userIdList
        );
        // 插入新关联信息
        if(!CollectionUtils.isEmpty(userResourceList)) {
            userResourceMapper.insertBatchSomeColumn(userResourceList);
        }

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

    private void checkParam(BatchAssignDTO batchAssignDTO) {
        if(batchAssignDTO.getAssignFlag() == null) {
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_BATCH_FLAG_CANNOT_BE_NULL);
        }
        if(CollectionUtils.isEmpty(batchAssignDTO.getUserIdList())) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        Integer projectId = batchAssignDTO.getProjectId();
        Integer resourceTypeId = batchAssignDTO.getResourceTypeId();
        if(projectId == null && resourceTypeId != null) {
            // 资源类别id不为null，则项目id不可为null
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_ERROR_2);
        }
        if(ControlLevelCode.getByType(batchAssignDTO.getControlLevel()) == null) {
            throw new SecurityException(ResultCode.RESOURCE_INVALID_CONTROL_LEVEL);
        }
    }

    private void checkParam(AssignToManyUserDTO assignToManyUserDTO) {
        checkParam(
                assignToManyUserDTO.getControlLevel(), assignToManyUserDTO.getProjectId(),
                assignToManyUserDTO.getResourceTypeId(), assignToManyUserDTO.getResourceId()
        );
    }

    //--------------------------资源权限管理（按用户管理）begin--------------------------

    @Override
    public PagingData<MByUVO> getManageByUserPage(MByUQueryDTO queryVo) {
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        IPage<UserPO> userPage = new Page<>(queryVo.getPage(), queryVo.getSize());

        // 拼接查询条件
        List<Integer> deptIdList = deptService.getChildDeptIdListByParentId(queryVo.getDeptId());
        userWrapper
                .like(queryVo.getUsername() != null, "username", queryVo.getUsername())
                .like(queryVo.getRealName() != null, "real_name", queryVo.getRealName())
                .in(queryVo.getDeptId() != null, "dept_id", deptIdList);
        userMapper.selectPage(userPage, userWrapper);

        List<MByUVO> list = new ArrayList<>();

        QueryWrapper<UserResourcePO> userResourceWrapper = new QueryWrapper<>();
        // 判断 资源查看控制权限 是否开启
        boolean isOn = getViewPermissionControlStatus();
        for(UserPO userPO : userPage.getRecords()) {
            MByUVO dataVo = CopyBeanUtil.copy(userPO, MByUVO.class);
            dataVo.setUserId(userPO.getId());
            // 设置部门信息
            dataVo.setDeptList(deptService.getParentDeptListByChildId(userPO.getDeptId()));
            // 计算管理权限资源数
            userResourceWrapper
                    .eq("user_id", userPO.getId())
                    .eq("control_level", ControlLevelCode.ADMIN.getType());
            dataVo.setAdminResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
            userResourceWrapper.clear();

            // 如果 资源查看控制权限 没开启，就不计算了
            if(isOn) {
                // 计算查看权限资源数
                userResourceWrapper
                        .eq("user_id", userPO.getId())
                        .eq("control_level", ControlLevelCode.VIEW.getType());
                dataVo.setViewResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
                userResourceWrapper.clear();
            }

            list.add(dataVo);
        }
        return new PagingData<>(list, userPage);
    }

    //--------------------------资源权限管理（按用户管理）end--------------------------

    //--------------------------资源权限管理（按资源管理）begin--------------------------

    @Override
    public PagingData<MByRVO> getManageByResourcePage(MByRQueryDTO queryVo) {
        // 检查参数
        checkParam(queryVo);

        // 判断 资源查看控制权限 是否开启
        boolean isOn = getViewPermissionControlStatus();

        if(queryVo.getShowLevel().equals(ShowLevelCode.PROJECT.getType())) {
            // 项目展示级别，表示查找所有项目
            return dealProjectLevel(queryVo, queryVo.getShowLevel(), isOn);
        } else if(queryVo.getShowLevel().equals(ShowLevelCode.RESOURCE_TYPE.getType())) {
            // 资源类别展示级别，表示查找某个项目下所有资源类别
            return dealResourceTypeLevel(queryVo, queryVo.getShowLevel(), isOn);
        } else {
            // 具体资源展示级别，表示查找该项目下该资源类别对应的资源
            return dealResourceLevel(queryVo, queryVo.getShowLevel(), isOn);
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
            QueryWrapper<ProjectPO> queryWrapper = new QueryWrapper<>();
            if(projectMapper.selectCount(queryWrapper.eq("id", projectId)) == 0) {
                // 无效的项目id
                throw new SecurityException(ResultCode.PROJECT_NOT_EXIST);
            }
        } else if(showLevel.equals(ShowLevelCode.RESOURCE.getType())){
            // 具体资源展示级别，表示查找该项目下该资源类别对应的资源
            if(projectId == null || resourceTypeId == null) {
                // 3级展示级别，项目id或资源类别id不可为null
                throw new SecurityException(ResultCode.RESOURCE_SHOW_LEVEL_ERROR_2);
            }
        }
    }

    private void checkParam(MByRQueryDTO queryVo) {
        checkParam(queryVo.getShowLevel(), queryVo.getProjectId(), queryVo.getResourceTypeId());
    }

    /**
     * 资源权限管理>按资源管理的列表信息
     * 封装PagingData
     * @param list 数据
     * @param pagination 分页信息
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> buildPagingData(List<MByRVO> list, PagingData.Pagination pagination) {
        PagingData<MByRVO> pagingData = new PagingData<>();
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
    private void wrapQueryCriteria(QueryWrapper<UserResourcePO> queryWrapper, int showLevel,
                                 int controlLevel, Object ... ids) {
        // QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.clear();
        // 拼接管理权限或查看权限条件
        queryWrapper.eq("control_level", controlLevel);

        // 根据展示级别拼接条件
        if(showLevel >= ShowLevelCode.PROJECT.getType()) {
            // 具体项目级别
            queryWrapper.eq(ids[0] != null, "project_id", ids[0]);
        }
        if(showLevel >= ShowLevelCode.RESOURCE_TYPE.getType()) {
            // 具体资源类别级别
            queryWrapper.eq(ids[1] != null, "resource_type_id", ids[1]);
        }
        if(showLevel >= ShowLevelCode.RESOURCE.getType()) {
            // 具体资源级别
            queryWrapper.eq(ids[2] != null, "resource_id", ids[2]);
        }
    }

    /**
     * 资源权限管理>按资源管理的列表信息>项目级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @param isOn 资源查看控制权限是否开启
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> dealProjectLevel(MByRQueryDTO queryVo, int showLevel, boolean isOn) {
        List<MByRVO> list = new ArrayList<>();
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();

        IPage<ProjectPO> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(queryVo.getName())) {
            // 如果有名字查询条件
            projectWrapper.like("project_name", queryVo.getName());
        }
        projectMapper.selectPage(iPage, projectWrapper);

        for(ProjectPO projectPO : iPage.getRecords()) {
            MByRVO data = new MByRVO();
            data.setProjectId(projectPO.getId());
            data.setProjectCode(projectPO.getProjectCode());
            data.setProjectName(projectPO.getProjectName());

            // 封装获取管理权限用户数条件 TODO
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN.getType(), projectPO.getId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            if(isOn) {
                // 封装查看权限用户数条件
                wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW.getType(), projectPO.getId());
                data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));
            }

            list.add(data);
        }
        return new PagingData<>(list, iPage);
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源列别级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @param isOn 资源查看控制权限是否开启
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> dealResourceTypeLevel(MByRQueryDTO queryVo, int showLevel, boolean isOn) {
        List<MByRVO> list = new ArrayList<>();
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();

        IPage<ResourceTypePO> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<ResourceTypePO> resourceTypeWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(queryVo.getName())) {
            // 如果有名字查询条件
            resourceTypeWrapper.like("type_name", queryVo.getName());
        }
        resourceTypeMapper.selectPage(iPage, resourceTypeWrapper);

        // 获取项目信息
        ProjectPO projectPO = projectMapper.selectById(queryVo.getProjectId());
        for(ResourceTypePO resourceTypePO : iPage.getRecords()) {
            MByRVO data = new MByRVO();
            data.setResourceTypeId(resourceTypePO.getId());
            data.setResourceTypeName(resourceTypePO.getTypeName());
            data.setProjectId(queryVo.getProjectId());
            data.setProjectName(projectPO.getProjectName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN.getType(),
                    projectPO.getId(), resourceTypePO.getId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            if(isOn) {
                // 封装查看权限用户数条件
                wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW.getType(),
                        projectPO.getId(), resourceTypePO.getId());
                data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));
            }

            list.add(data);
        }
        return new PagingData<>(list, iPage);
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @param isOn 资源查看控制权限是否开启
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> dealResourceLevel(MByRQueryDTO queryVo, int showLevel, boolean isOn) {
        // 调用扩展接口获取具体资源信息
        PagingData<ResourceDto> page = resourceExtend.getResourcePage(
                queryVo.getProjectId(), queryVo.getResourceTypeId(),
                queryVo.getName(), queryVo.getPage(), queryVo.getSize()
        );
        List<MByRVO> list = new ArrayList<>();
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        // 获取资源类别信息
        ResourceTypePO resourceTypePO = resourceTypeMapper.selectById(queryVo.getResourceTypeId());

        for(ResourceDto resourceDto : page.getBizData()) {
            MByRVO data = new MByRVO();

            data.setResourceTypeId(resourceTypePO.getId());
            data.setResourceTypeName(resourceTypePO.getTypeName());
            data.setProjectId(queryVo.getProjectId());
            data.setResourceId(resourceDto.getResourceId());
            data.setResourceName(resourceDto.getResourceName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN.getType(),
                    queryVo.getProjectId(), resourceTypePO.getId(), resourceDto.getResourceId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            if(isOn) {
                // 封装查看权限用户数条件
                wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW.getType(),
                        queryVo.getProjectId(), resourceTypePO.getId(), resourceDto.getResourceId());
                data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));
            }

            list.add(data);
        }
        return buildPagingData(list, page.getPagination());
    }

    //--------------------------资源权限管理（按资源管理）end--------------------------
}
