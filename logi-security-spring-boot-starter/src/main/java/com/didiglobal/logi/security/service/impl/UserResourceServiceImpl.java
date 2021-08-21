package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.resource.*;
import com.didiglobal.logi.security.common.dto.resource.type.ResourceTypeQueryDTO;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.dto2.ResourceDTO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.enums.resource.HasLevelCode;
import com.didiglobal.logi.security.common.enums.resource.ShowLevelCode;
import com.didiglobal.logi.security.common.po.ProjectPO;
import com.didiglobal.logi.security.common.po.UserResourcePO;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.resource.*;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.mapper.UserResourceMapper;
import com.didiglobal.logi.security.service.*;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class UserResourceServiceImpl implements UserResourceService {


    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private OplogService oplogService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    @Autowired
    private ResourceExtend resourceExtend;

    /**
     * 封装UserResource的查询条件
     * @param controlLevel 资源权限级别
     * @param userId 用户id
     * @param projectId 项目id
     * @param resourceTypeId 资源类别id
     * @param resourceId 具体资源id
     * @return QueryWrapper<UserResourcePO>
     */
    private QueryWrapper<UserResourcePO> wrapQueryCriteria(int controlLevel,
                                                           Integer userId, Integer projectId,
                                                           Integer resourceTypeId, Integer resourceId) {
        QueryWrapper<UserResourcePO> queryWrapper = new QueryWrapper<>();
        // 拼接管理权限或查看权限条件
        queryWrapper
                .eq("control_level", controlLevel)
                .eq(userId != null, "user_id", userId)
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .eq(resourceId != null, "resource_id", resourceId);
        return queryWrapper;
    }

    @Override
    public int getResourceCntByUserId(Integer userId, UserResourceQueryDTO queryDTO) {
        QueryWrapper<UserResourcePO> queryWrapper =
                wrapQueryCriteria(queryDTO.getControlLevel(), userId, queryDTO.getProjectId(), queryDTO.getResourceTypeId(), queryDTO.getResourceId());
        return userResourceMapper.selectCount(queryWrapper);
    }

    /**
     * VPC（ViewPermissionControl）
     * 判断资源查看权限控制状态是否开启
     * 只要数据库中有这样的记录（user_id、project_id、resource_type_id、resource_id、control_level）都为0
     * 则开启了
     */
    private QueryWrapper<UserResourcePO> buildVPCStatusQueryWrapper() {
        return wrapQueryCriteria(0, 0, 0, 0, 0);
    }

    @Override
    public boolean getViewPermissionControlStatus() {
        QueryWrapper<UserResourcePO> queryWrapper = buildVPCStatusQueryWrapper();
        // 判断是否有记录
        return userResourceMapper.selectCount(queryWrapper) > 0;
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
            List<ProjectBriefVO> projectBriefVOList = projectService.getProjectBriefList();
            for(ProjectBriefVO projectBriefVO : projectBriefVOList) {
                MByUDataVO dataVo = new MByUDataVO(projectBriefVO.getId(), projectBriefVO.getProjectName());
                dataVo.setHasLevel(getHasLevel(
                        isBatch, showLevel, controlLevel, userId, projectBriefVO.getId(), null, null
                ).getType());
                resultList.add(dataVo);
            }
        } else if(ShowLevelCode.RESOURCE_TYPE.getType().equals(showLevel)) {
            // 如果是资源类别展示级别
            List<ResourceTypeVO> resourceTypeVOList = resourceTypeService.getAllResourceTypeList();
            for(ResourceTypeVO resourceTypeVO : resourceTypeVOList) {
                MByUDataVO dataVo = new MByUDataVO(resourceTypeVO.getId(), resourceTypeVO.getTypeName());
                dataVo.setHasLevel(getHasLevel(
                        isBatch, showLevel, controlLevel, userId, projectId, resourceTypeVO.getId(), null
                ).getType());
                resultList.add(dataVo);
            }
        } else {
            // 如果是具体资源展示级别
            List<ResourceDTO> resourceDTOList = resourceExtend.getResourceList(projectId, resourceTypeId);
            for(ResourceDTO resourceDto : resourceDTOList) {
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

        List<UserBriefVO> UserBriefVOList = userService.getUserBriefListByUsernameOrRealName(queryVo.getName());

        List<MByRDataVO> result = new ArrayList<>();
        for(UserBriefVO userBriefVO : UserBriefVOList) {
            MByRDataVO dataVo = new MByRDataVO();
            dataVo.setUserId(userBriefVO.getId());
            dataVo.setUsername(userBriefVO.getUsername());
            dataVo.setRealName(userBriefVO.getRealName());
            dataVo.setHasLevel(getHasLevel(
                    isBatch, ShowLevelCode.RESOURCE.getType(), controlLevel, userBriefVO.getId(), projectId, resourceTypeId, resourceId
            ).getType());
            result.add(dataVo);
        }
        return result;
    }

    /**
     * 获取用户userId，对该数据（项目、资源类别、具体资源）的拥有级别
     * 拥有级别：全拥有、半拥有、不拥有
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
        UserResourceQueryDTO queryDTO = new UserResourceQueryDTO();
        queryDTO.setShowLevel(showLevel);
        queryDTO.setControlLevel(controlLevel);
        queryDTO.setProjectId(projectId);
        queryDTO.setResourceTypeId(resourceTypeId);
        queryDTO.setResourceId(resourceId);
        int hasResourceCnt = getResourceCntByUserId(userId, queryDTO);
        if(hasResourceCnt == 0) {
            return HasLevelCode.NONE;
        } else {
            // 获取该项目下具体资源的个数
            int resourceCnt = 1;
            if(resourceId == null) {
                // 如果具体资源id为null，则获取某个项目下 || 某个项目下某个资源类别的具体资源个数
                resourceCnt = resourceExtend.getResourceCnt(projectId, resourceTypeId);
                return hasResourceCnt == resourceCnt ? HasLevelCode.ALL : HasLevelCode.HALF;
            }
            // resourceId != null，则cnt最大为1
            return hasResourceCnt == resourceCnt ? HasLevelCode.ALL : HasLevelCode.NONE;
        }
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
        if(userId == null) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
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
        List<Integer> projectIdList;
        List<Integer> resourceTypeIdList;
        List<Integer> resourceIdList = null;
        QueryWrapper<ProjectPO> projectWrapper = new QueryWrapper<>();
        projectWrapper.select("id");

        if(projectId == null) {
            // 说明idList是项目的idList
            projectIdList = new ArrayList<>(idList);
            resourceTypeIdList = resourceTypeService.getAllResourceTypeIdList();
        } else if(resourceTypeId == null) {
            // 说明idList是资源类别idList
            projectIdList = new ArrayList<Integer>(){{ add(projectId); }};
            resourceTypeIdList = new ArrayList<>(idList);
        } else {
            // 说明idList是具体资源idList
            projectIdList = new ArrayList<Integer>(){{ add(projectId); }};
            resourceTypeIdList = new ArrayList<Integer>(){{ add(resourceTypeId); }};
            resourceIdList = new ArrayList<>(idList);
        }

        // 封装List<ResourceDto>
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        for(Integer pId : projectIdList) {
            for(Integer rtId : resourceTypeIdList) {
                if(resourceIdList == null) {
                    resourceDTOList.addAll(resourceExtend.getResourceList(pId, rtId));
                    continue;
                }
                for(Integer rId : resourceIdList) {
                    resourceDTOList.add(new ResourceDTO(pId, rtId, rId));
                }
            }
        }
        return buildUserResourceList(controlLevel, userIdList, resourceDTOList);
    }

    private List<UserResourcePO> buildUserResourceList(int controlLevel, List<Integer> userIdList,
                                                       List<ResourceDTO> resourceDTOList) {
        List<UserResourcePO> userResourcePOList =  new ArrayList<>();
        for(Integer userId : userIdList) {
            for(ResourceDTO resourceDTO : resourceDTOList) {
                UserResourcePO userResourcePO = new UserResourcePO(resourceDTO);
                userResourcePO.setUserId(userId);
                userResourcePO.setControlLevel(controlLevel);
                userResourcePOList.add(userResourcePO);
            }
        }
        return userResourcePOList;
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
        List<Integer> userIdList = new ArrayList<Integer>(){{ add(userId); }};
        List<UserResourcePO> userResourceList = getUserResourceList(
                projectId, resourceTypeId, controlLevel, idList, userIdList
        );
        // 插入new关联信息
        if(!CollectionUtils.isEmpty(userResourceList)) {
            userResourceMapper.insertBatchSomeColumn(userResourceList);
        }

        // 保存操作日志 TODO：用户+资源名称 这个信息咋搞比较好，还要记录移除的信息
        oplogService.saveOplog(OplogDTO.builder()
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
        int controlLevel = assignToManyUserDTO.getControlLevel();

        QueryWrapper<UserResourcePO> queryWrapper = wrapQueryCriteria(
                assignToManyUserDTO.getControlLevel(), null, projectId, resourceTypeId, resourceId
        );
        // 删除old关联信息
        userResourceMapper.delete(queryWrapper);

        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        if(resourceId == null) {
            // resourceId为null，说明是某个项目或者某个资源类别下的全部具体资源的权限分配给用户，获取所有的具体资源信息
            resourceDTOList.addAll(resourceExtend.getResourceList(projectId, resourceTypeId));
        } else {
            // 说明只有一个资源的权限分配给用户
            resourceDTOList.add(new ResourceDTO(projectId, resourceTypeId, resourceId));
        }
        // 插入new关联信息
        List<UserResourcePO> userResourcePOList = buildUserResourceList(controlLevel, userIdList, resourceDTOList);
        if(!CollectionUtils.isEmpty(userResourcePOList)) {
            userResourceMapper.insertBatchSomeColumn(userResourcePOList);
        }

        // 保存操作日志 TODO：资源名称+用户 这个信息咋搞比较好？还要记录移除的信息
        oplogService.saveOplog(OplogDTO.builder()
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
    public void batchAssignResourcePermission(BatchAssignDTO assignDTO) {
        // 检查参数
        checkParam(assignDTO);
        // 获取参数
        List<Integer> userIdList = assignDTO.getUserIdList();
        List<Integer> idList = assignDTO.getIdList();
        int controlLevel = assignDTO.getControlLevel();
        boolean assignFlag = assignDTO.getAssignFlag();
        // 先删除全部old关联信息
        deleteOldRelationBeforeBatchAssign(
                assignDTO.getProjectId(), assignDTO.getResourceTypeId(), assignFlag, controlLevel, idList
        );
        // 获取新管理信息
        List<UserResourcePO> userResourcePOList = getUserResourceList(
                assignDTO.getProjectId(), assignDTO.getResourceTypeId(), controlLevel, idList, userIdList
        );
        // 插入新关联信息
        if(!CollectionUtils.isEmpty(userResourcePOList)) {
            userResourceMapper.insertBatchSomeColumn(userResourcePOList);
        }

        if(assignFlag) {
            // 保存操作日志 TODO：资源名称+用户 这个信息咋搞比较好？还要记录移除的信息
            oplogService.saveOplog(OplogDTO.builder()
                    .operatePage("资源权限管理").operateType("批量分配用户")
                    .targetType("资源").target("资源名称+用户").build()
            );
        } else {
            // 保存操作日志 TODO：用户+资源名称 这个信息咋搞比较好？还要记录移除的信息
            oplogService.saveOplog(OplogDTO.builder()
                    .operatePage("资源权限管理").operateType("批量分配资源")
                    .targetType("用户").target("用户+资源名称").build()
            );
        }
    }

    private void checkParam(BatchAssignDTO assignDTO) {
        if(assignDTO.getAssignFlag() == null) {
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_BATCH_FLAG_CANNOT_BE_NULL);
        }
        if(assignDTO.getProjectId() == null && assignDTO.getResourceTypeId() != null) {
            // 资源类别id不为null，则项目id不可为null
            throw new SecurityException(ResultCode.RESOURCE_ASSIGN_ERROR_2);
        }
        if(ControlLevelCode.getByType(assignDTO.getControlLevel()) == null) {
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
        PagingData<UserBriefVO> userPage = userService.getUserBriefPage(new UserBriefQueryDTO(queryVo));
        List<MByUVO> result = new ArrayList<>();
        QueryWrapper<UserResourcePO> userResourceWrapper = new QueryWrapper<>();
        // 判断 资源查看控制权限 是否开启
        boolean isOn = getViewPermissionControlStatus();
        for(UserBriefVO userBriefVO : userPage.getBizData()) {
            MByUVO dataVo = CopyBeanUtil.copy(userBriefVO, MByUVO.class);
            dataVo.setUserId(userBriefVO.getId());
            // 设置部门信息
            dataVo.setDeptList(deptService.getDeptBriefListByChildId(userBriefVO.getDeptId()));
            // 计算管理权限资源数
            userResourceWrapper
                    .eq("user_id", userBriefVO.getId())
                    .eq("control_level", ControlLevelCode.ADMIN.getType());
            dataVo.setAdminResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
            userResourceWrapper.clear();

            // 如果 资源查看控制权限 没开启，就不计算了
            if(isOn) {
                // 计算查看权限资源数
                userResourceWrapper
                        .eq("user_id", userBriefVO.getId())
                        .eq("control_level", ControlLevelCode.VIEW.getType());
                dataVo.setViewResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
                userResourceWrapper.clear();
            }

            result.add(dataVo);
        }
        return new PagingData<>(result, userPage.getPagination());
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
            return dealProjectLevel(queryVo, isOn);
        } else if(queryVo.getShowLevel().equals(ShowLevelCode.RESOURCE_TYPE.getType())) {
            // 资源类别展示级别，表示查找某个项目下所有资源类别
            return dealResourceTypeLevel(queryVo, isOn);
        } else {
            // 具体资源展示级别，表示查找该项目下该资源类别对应的资源
            return dealResourceLevel(queryVo, isOn);
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

    private void checkParam(MByRQueryDTO queryVo) {
        checkParam(queryVo.getShowLevel(), queryVo.getProjectId(), queryVo.getResourceTypeId());
    }

    /**
     * 资源权限管理>按资源管理的列表信息>项目级别
     * @param queryVo 查询条件
     * @param isOn 资源查看控制权限是否开启
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> dealProjectLevel(MByRQueryDTO queryVo, boolean isOn) {
        ProjectBriefQueryDTO queryDTO = new ProjectBriefQueryDTO(queryVo);
        PagingData<ProjectBriefVO> projectPage = projectService.getProjectBriefPage(queryDTO);

        List<MByRVO> list = new ArrayList<>();
        for(ProjectBriefVO projectBriefVO : projectPage.getBizData()) {
            MByRVO data = new MByRVO();
            data.setProjectId(projectBriefVO.getId());
            data.setProjectCode(projectBriefVO.getProjectCode());
            data.setProjectName(projectBriefVO.getProjectName());

            // 获取管理权限用户数
            QueryWrapper<UserResourcePO> queryWrapper =
                    wrapQueryCriteria(ControlLevelCode.ADMIN.getType(), null, projectBriefVO.getId(), null, null);
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            if(isOn) {
                // 获取查看权限用户数
                queryWrapper =
                        wrapQueryCriteria(ControlLevelCode.VIEW.getType(), null, projectBriefVO.getId(), null, null);
                data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));
            }
            list.add(data);
        }
        return new PagingData<>(list, projectPage.getPagination());
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源列别级别
     * @param queryVo 查询条件
     * @param isOn 资源查看控制权限是否开启
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> dealResourceTypeLevel(MByRQueryDTO queryVo, boolean isOn) {
        List<MByRVO> list = new ArrayList<>();

        ResourceTypeQueryDTO queryDTO = new ResourceTypeQueryDTO(queryVo);
        PagingData<ResourceTypeVO> resourceTypePage = resourceTypeService.getResourceTypePage(queryDTO);

        // 获取项目信息
        ProjectBriefVO projectBriefVO = projectService.getProjectBriefByProjectId(queryVo.getProjectId());
        for(ResourceTypeVO resourceTypeVO : resourceTypePage.getBizData()) {
            MByRVO data = new MByRVO();
            data.setResourceTypeId(resourceTypeVO.getId());
            data.setResourceTypeName(resourceTypeVO.getTypeName());
            data.setProjectId(queryVo.getProjectId());
            data.setProjectName(projectBriefVO.getProjectName());

            // 获取管理权限用户数
            QueryWrapper<UserResourcePO> queryWrapper =
                    wrapQueryCriteria(ControlLevelCode.ADMIN.getType(), null, queryVo.getProjectId(), resourceTypeVO.getId(), null);
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            if(isOn) {
                // 获取查看权限用户数
                queryWrapper =
                        wrapQueryCriteria(ControlLevelCode.VIEW.getType(), null, queryVo.getProjectId(), resourceTypeVO.getId(), null);
                data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));
            }
            list.add(data);
        }
        return new PagingData<>(list, resourceTypePage.getPagination());
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源级别
     * @param queryVo 查询条件
     * @param isOn 资源查看控制权限是否开启
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<MByRVO> dealResourceLevel(MByRQueryDTO queryVo, boolean isOn) {
        // 调用扩展接口获取具体资源信息
        PagingData<ResourceDTO> page = resourceExtend.getResourcePage(
                queryVo.getProjectId(), queryVo.getResourceTypeId(),
                queryVo.getName(), queryVo.getPage(), queryVo.getSize()
        );
        List<MByRVO> list = new ArrayList<>();
        // 获取资源类别信息
        ResourceTypeVO resourceTypeVO = resourceTypeService.getResourceTypeByResourceTypeId(queryVo.getResourceTypeId());

        for(ResourceDTO resourceDto : page.getBizData()) {
            MByRVO data = new MByRVO();
            data.setResourceTypeId(resourceTypeVO.getId());
            data.setResourceTypeName(resourceTypeVO.getTypeName());
            data.setProjectId(queryVo.getProjectId());
            data.setResourceId(resourceDto.getResourceId());
            data.setResourceName(resourceDto.getResourceName());

            // 获取管理权限用户数
            QueryWrapper<UserResourcePO> queryWrapper =
                    wrapQueryCriteria(ControlLevelCode.ADMIN.getType(), null, queryVo.getProjectId(), resourceTypeVO.getId(), resourceDto.getResourceId());
            data.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            if(isOn) {
                // 获取查看权限用户数
                queryWrapper =
                        wrapQueryCriteria(ControlLevelCode.VIEW.getType(), null, queryVo.getProjectId(), resourceTypeVO.getId(), resourceDto.getResourceId());
                data.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));
            }
            list.add(data);
        }
        return new PagingData<>(list, page.getPagination());
    }

    //--------------------------资源权限管理（按资源管理）end--------------------------

}
