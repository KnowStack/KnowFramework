package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.ResourceDto;
import com.didiglobal.logi.security.common.entity.Project;
import com.didiglobal.logi.security.common.entity.ResourceType;
import com.didiglobal.logi.security.common.entity.UserResource;
import com.didiglobal.logi.security.common.enums.resource.ControlLevelCode;
import com.didiglobal.logi.security.common.enums.resource.ManageByResourceCode;
import com.didiglobal.logi.security.common.vo.resource.ResourceQueryVo;
import com.didiglobal.logi.security.common.vo.resource.ResourceTypeVo;
import com.didiglobal.logi.security.common.vo.resource.ManageByResourceVo;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.mapper.ProjectMapper;
import com.didiglobal.logi.security.mapper.ResourceTypeMapper;
import com.didiglobal.logi.security.mapper.UserResourceMapper;
import com.didiglobal.logi.security.service.ResourceService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Override
    public PagingData<ManageByResourceVo> getPageManageByResource(ResourceQueryVo queryVo) {
        if(queryVo.getProjectId() == null) {
            // 如果查询条件项目id为null，表示查找所有项目（全部项目级别）
            return dealProjectLevel(queryVo);
        } else if(queryVo.getResourceTypeId() == null) {
            // 如果查询条件资源类别id为null，表示查找某个项目下所有资源类别（具体项目级别）
            return dealResourceTypeLevel(queryVo);
        } else {
            // 如果查询条件项目id和资源类别id都不为null，表示查找该项目下该资源类别对应的资源（具体资源类型级别）
            return dealResourceLevel(queryVo);
        }
    }

    /**
     * 资源权限管理>按资源管理的列表信息
     * 封装PagingData
     * @param list 数据
     * @param args total, pages, pageNo, pageSize
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> buildPagingData(List<ManageByResourceVo> list, long ... args) {
        PagingData<ManageByResourceVo> pagingData = new PagingData<>();
        pagingData.setPagination(PagingData.buildPagination(args[0], args[1], args[2], args[3]));
        pagingData.setBizData(list);
        return pagingData;
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
     * @param queryVo 查询条件
     * @param controlLevelCode 资源权限级别
     * @param obj project_id、resource_type_id、resource_id
     */
    private void wrapQueryCriteria(QueryWrapper<UserResource> queryWrapper, ResourceQueryVo queryVo,
                                 ControlLevelCode controlLevelCode, Object ... obj) {
        queryWrapper.clear();
        // 拼接 管理或查看 条件
        queryWrapper.eq("control_level", controlLevelCode.getType());

        // 获取按资源管理列表展示级别
        int level;
        if(queryVo.getProjectId() == null) {
            level = ManageByResourceCode.PROJECT.getType();
        } else if(queryVo.getResourceTypeId() == null) {
            level = ManageByResourceCode.RESOURCE_TYPE.getType();
        } else {
            level = ManageByResourceCode.RESOURCE.getType();
        }

        // 拼接条件
        if(level >= ManageByResourceCode.PROJECT.getType()) {
            // 具体项目级别
            queryWrapper.eq("project_id", obj[0]);
        }
        if(level >= ManageByResourceCode.RESOURCE_TYPE.getType()) {
            // 具体资源类别级别
            queryWrapper.eq("resource_type_id", obj[1]);
        }
        if(level >= ManageByResourceCode.RESOURCE.getType()) {
            // 具体资源级别
            queryWrapper.eq("resource_id", obj[2]);
        }
    }

    /**
     * 资源权限管理>按资源管理的列表信息>项目级别
     * @param queryVo 查询条件
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> dealProjectLevel(ResourceQueryVo queryVo) {
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
            wrapQueryCriteria(queryWrapper, queryVo, ControlLevelCode.ADMIN, project.getId());
            manageByResourceVo.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, queryVo, ControlLevelCode.VIEW, project);
            manageByResourceVo.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(manageByResourceVo);
        }
        return buildPagingData(list, iPage.getTotal(), iPage.getPages(), iPage.getCurrent(), iPage.getSize());
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源列别级别
     * @param queryVo 查询条件
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> dealResourceTypeLevel(ResourceQueryVo queryVo) {
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
            wrapQueryCriteria(queryWrapper, queryVo, ControlLevelCode.ADMIN, project.getId(), resourceType.getId());
            manageByResourceVo.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, queryVo, ControlLevelCode.VIEW, project, resourceType);
            manageByResourceVo.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(manageByResourceVo);
        }
        return buildPagingData(list, iPage.getTotal(), iPage.getPages(), iPage.getCurrent(), iPage.getSize());
    }

    /**
     * 资源权限管理>按资源管理的列表信息>资源级别
     * @param queryVo 查询条件
     * @return PagingData<ManageByResourceVo>
     */
    private PagingData<ManageByResourceVo> dealResourceLevel(ResourceQueryVo queryVo) {
        PagingData<ResourceDto> page = resourceExtend.getResourceList(
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
            wrapQueryCriteria(queryWrapper, queryVo, ControlLevelCode.ADMIN,
                    queryVo.getProjectId(), resourceType.getId(), resourceDto.getResourceId());
            manageByResourceVo.setAdminUserCnt(userResourceMapper.selectCount(queryWrapper));

            // 封装查看权限用户数条件
            wrapQueryCriteria(queryWrapper, queryVo, ControlLevelCode.VIEW,
                    queryVo.getProjectId(), resourceType.getId(), resourceDto.getResourceId());
            manageByResourceVo.setViewUserCnt(userResourceMapper.selectCount(queryWrapper));

            list.add(manageByResourceVo);
        }
        return buildPagingData(list, page.getPagination());
    }

    @Override
    public List<ResourceTypeVo> getResourceTypeList() {
        List<ResourceType> resourceTypeList = resourceTypeMapper.selectList(null);
        return CopyBeanUtil.copyList(resourceTypeList, ResourceTypeVo.class);
    }
}
