package com.didiglobal.logi.security.extend.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.resource.ResourceDTO;
import com.didiglobal.logi.security.inside.common.po.ProjectResourcePO;
import com.didiglobal.logi.security.extend.ResourceExtend;
import com.didiglobal.logi.security.dao.mapper.ProjectMapper;
import com.didiglobal.logi.security.inside.mapper.ProjectResourceMapper;
import com.didiglobal.logi.security.dao.mapper.ResourceTypeMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

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
    public PagingData<ResourceDTO> getResourcePage(Integer projectId, Integer resourceTypeId, String resourceName, int page, int size) {

        IPage<ProjectResourcePO> iPage = new Page<>(page, size);
        QueryWrapper<ProjectResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId)
                .like(!StringUtils.isEmpty(resourceName), "resource_name", resourceName);
        projectResourceMapper.selectPage(iPage, queryWrapper);
        List<ResourceDTO> resourceDTOList = CopyBeanUtil.copyList(iPage.getRecords(), ResourceDTO.class);
        return new PagingData<>(resourceDTOList, iPage);
    }

    @Override
    public List<ResourceDTO> getResourceList(Integer projectId, Integer resourceTypeId) {
        QueryWrapper<ProjectResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId);
        List<ProjectResourcePO> projectResourcePOList = projectResourceMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(projectResourcePOList, ResourceDTO.class);
    }

    @Override
    public int getResourceCnt(Integer projectId, Integer resourceTypeId) {
        QueryWrapper<ProjectResourcePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(projectId != null, "project_id", projectId)
                .eq(resourceTypeId != null, "resource_type_id", resourceTypeId);
        return projectResourceMapper.selectCount(queryWrapper);
    }
}
