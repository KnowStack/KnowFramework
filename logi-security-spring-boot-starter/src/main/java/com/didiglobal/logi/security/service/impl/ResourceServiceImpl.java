package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.ResourceDto;
import com.didiglobal.logi.security.common.entity.*;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.enums.resource.ShowLevelCode;
import com.didiglobal.logi.security.common.vo.resource.*;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.mapper.ProjectMapper;
import com.didiglobal.logi.security.mapper.ResourceTypeMapper;
import com.didiglobal.logi.security.mapper.UserMapper;
import com.didiglobal.logi.security.mapper.UserResourceMapper;
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

    private ResourceExtend resourceExtend;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Override
    public List<ResourceTypeVo> getResourceTypeList() {
        List<ResourceType> resourceTypeList = resourceTypeMapper.selectList(null);
        return CopyBeanUtil.copyList(resourceTypeList, ResourceTypeVo.class);
    }

    @Override
    public List<ResourceVo> getResourceList(Integer projectId, Integer resourceTypeId) {
        List<ResourceDto> resourceDtoList = resourceExtend.getResourceList(projectId, resourceTypeId);
        return CopyBeanUtil.copyList(resourceDtoList, ResourceVo.class);
    }

    @Override
    public void assignResourcePermission(AssignToOneUserVo assignToOneUserVo) {
        Integer userId = assignToOneUserVo.getUserId();
        if(userId == null || userMapper.selectById(userId) == null) {
            throw new SecurityException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        List<UserResource> userResourceList = new ArrayList<>();


    }

    @Override
    public void assignResourcePermission(AssignToManyUserVo assignToManyUserVo) {
        List<Integer> userIdList = assignToManyUserVo.getUserIdList();
        Integer projectId = assignToManyUserVo.getProjectId();
        Integer resourceTypeId = assignToManyUserVo.getResourceTypeId();
        Integer resourceId = assignToManyUserVo.getResourceId();
        if(projectId == null) {
            // 项目id不可为null
            throw new SecurityException(ResultCode.PROJECT_ID_CANNOT_BE_NULL);
        }
        if(resourceTypeId == null && resourceId != null) {
            // 这种情况不允许出现（如果resourceId != null，则resourceTypeId必不为null）
            throw new SecurityException(ResultCode.RESOURCE_PERMISSION_ASSIGN_ERROR);
        }

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
            ResourceDto resourceDto = new ResourceDto();
            ResourceDto.builder().projectId(projectId).resourceTypeId(resourceTypeId).resourceId(resourceId);
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
    }

    //--------------------------资源权限管理（按用户管理）begin--------------------------

    @Override
    public PagingData<ManageByUserVo> getManageByUserPage(ManageByUserQueryVo queryVo) {
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        IPage<User> userPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 拼接查询条件
        userWrapper
                .eq(queryVo.getDeptId() != null, "dept_id", queryVo.getDeptId())
                .like(queryVo.getUsername() != null, "username", queryVo.getUsername())
                .like(queryVo.getDealName() != null, "deal_name", queryVo.getDealName());
        userMapper.selectPage(userPage, userWrapper);

        List<ManageByUserVo> manageByUserVoList = new ArrayList<>();

        QueryWrapper<UserResource> userResourceWrapper = new QueryWrapper<>();
        for(User user : userPage.getRecords()) {
            ManageByUserVo manageByUserVo = CopyBeanUtil.copy(user, ManageByUserVo.class);

            // 计算管理权限资源数
            userResourceWrapper
                    .eq("user_id", user.getId())
                    .eq("control_level", ControlLevelCode.ADMIN.getType());
            manageByUserVo.setAdminResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
            userResourceWrapper.clear();

            // 计算查看权限资源数
            userResourceWrapper
                    .eq("user_id", user.getId())
                    .eq("control_level", ControlLevelCode.VIEW.getType());
            manageByUserVo.setViewResourceCnt(userResourceMapper.selectCount(userResourceWrapper));
            userResourceWrapper.clear();

            manageByUserVoList.add(manageByUserVo);
        }
        return new PagingData<>(manageByUserVoList, userPage);
    }

    //--------------------------资源权限管理（按用户管理）end--------------------------

    //--------------------------资源权限管理（按资源管理）begin--------------------------

    @Override
    public PagingData<ManageByResourceVo> getManageByResourcePage(ManageByResourceQueryVo queryVo) {
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

    /**
     * 资源权限管理>按资源管理的列表信息
     * 封装PagingData
     * @param list 数据
     * @param pagination 分页信息
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> buildPagingData(List<ManageByResourceVo> list, PagingData.Pagination pagination) {
        PagingData<ManageByResourceVo> pagingData = new PagingData<>();
        pagingData.setPagination(pagination);
        pagingData.setBizData(list);
        return pagingData;
    }

    /**
     * 资源权限管理>按资源管理的列表信息
     * 根据级别拼装【管理权限用户数 或 管理权限用户数】的查询条件
     * @param queryWrapper 数据库查询条件
     * @param showLevel 按资源管理展示级别
     * @param controlLevelCode 资源权限级别
     * @param ids 各种id的集合，顺序为：project_id、resource_type_id、resource_id
     */
    private void wrapQueryCriteria(QueryWrapper<UserResource> queryWrapper, int showLevel,
                                 ControlLevelCode controlLevelCode, Object ... ids) {
        queryWrapper.clear();
        // 拼接管理权限或查看权限条件
        queryWrapper.eq("control_level", controlLevelCode.getType());

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
    private PagingData<ManageByResourceVo> dealProjectLevel(ManageByResourceQueryVo queryVo, int showLevel) {
        List<ManageByResourceVo> list = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();

        IPage<Project> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
        if(StringUtils.isEmpty(queryVo.getName())) {
            // 如果有名字查询条件
            projectWrapper.like("project_name", queryVo.getName());
        }
        projectMapper.selectPage(iPage, projectWrapper);

        for(Project project : iPage.getRecords()) {
            ManageByResourceVo manageByResourceVo = new ManageByResourceVo();
            manageByResourceVo.setValue1(project.getProjectCode());
            manageByResourceVo.setValue2(project.getProjectName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN, project.getId());
            manageByResourceVo.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW, project);
            manageByResourceVo.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(manageByResourceVo);
        }
        return new PagingData<>(list, iPage);
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源列别级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> dealResourceTypeLevel(ManageByResourceQueryVo queryVo, int showLevel) {
        List<ManageByResourceVo> list = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();

        IPage<ResourceType> iPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<ResourceType> resourceTypeWrapper = new QueryWrapper<>();
        if(StringUtils.isEmpty(queryVo.getName())) {
            // 如果有名字查询条件
            resourceTypeWrapper.like("type_name", queryVo.getName());
        }
        resourceTypeMapper.selectPage(iPage, resourceTypeWrapper);

        // 获取项目信息
        Project project = projectMapper.selectById(queryVo.getProjectId());
        for(ResourceType resourceType : iPage.getRecords()) {
            ManageByResourceVo manageByResourceVo = new ManageByResourceVo();
            manageByResourceVo.setValue1(resourceType.getTypeName());
            manageByResourceVo.setValue2(project.getProjectName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN, project.getId(), resourceType.getId());
            manageByResourceVo.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW, project.getId(), resourceType.getId());
            manageByResourceVo.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(manageByResourceVo);
        }
        return new PagingData<>(list, iPage);
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源级别
     * @param queryVo 查询条件
     * @param showLevel 展示级别
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> dealResourceLevel(ManageByResourceQueryVo queryVo, int showLevel) {
        // 调用扩展接口获取具体资源信息
        PagingData<ResourceDto> page = resourceExtend.getResourcePage(
                queryVo.getProjectId(),
                queryVo.getResourceTypeId(),
                queryVo.getName(),
                queryVo.getPage(),
                queryVo.getSize()
        );
        List<ManageByResourceVo> list = new ArrayList<>();
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        // 获取资源类别信息
        ResourceType resourceType = resourceTypeMapper.selectById(queryVo.getResourceTypeId());

        for(ResourceDto resourceDto : page.getBizData()) {
            ManageByResourceVo manageByResourceVo = new ManageByResourceVo();
            manageByResourceVo.setValue1(resourceDto.getResourceName());
            manageByResourceVo.setValue2(resourceType.getTypeName());

            // 封装获取管理权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.ADMIN,
                    queryVo.getProjectId(), resourceType.getId(), resourceDto.getResourceId());
            manageByResourceVo.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, showLevel, ControlLevelCode.VIEW,
                    queryVo.getProjectId(), resourceType.getId(), resourceDto.getResourceId());
            manageByResourceVo.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(manageByResourceVo);
        }
        return buildPagingData(list, page.getPagination());
    }

    //--------------------------资源权限管理（按资源管理）end--------------------------
}
