package com.didiglobal.logi.security.extend.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.ResourceDto;
import com.didiglobal.logi.security.common.entity.Project;
import com.didiglobal.logi.security.common.entity.ProjectResource;
import com.didiglobal.logi.security.common.entity.ResourceType;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.mapper.ProjectMapper;
import com.didiglobal.logi.security.mapper.ProjectResourceMapper;
import com.didiglobal.logi.security.mapper.ResourceTypeMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author cjm
 */
@Component
public class ResourceExtendImpl implements ResourceExtend {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Autowired
    private ProjectResourceMapper projectResourceMapper;

    @Override
    public PagingData<ResourceDto> getResourcePage(Integer projectId, Integer resourceTypeId, String resourceName, int page, int size) {

        IPage<ProjectResource> iPage = new Page<>(page, size);
        QueryWrapper<ProjectResource> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .like(!StringUtils.isEmpty(resourceName), "resource_name", resourceName);
        projectResourceMapper.selectPage(iPage, queryWrapper);
        List<ResourceDto> resourceDtoList = CopyBeanUtil.copyList(iPage.getRecords(), ResourceDto.class);
        return new PagingData<>(resourceDtoList, iPage);
    }

    @Override
    public List<ResourceDto> getResourceList(Integer projectId, Integer resourceTypeId) {
        QueryWrapper<ProjectResource> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId);
        List<ProjectResource> projectResources = projectResourceMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(projectResources, ResourceDto.class);
    }

    @Override
    public int getResourceCnt(Integer projectId, Integer resourceTypeId) {
        QueryWrapper<ProjectResource> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId);
        return projectResourceMapper.selectCount(queryWrapper);
    }
}
