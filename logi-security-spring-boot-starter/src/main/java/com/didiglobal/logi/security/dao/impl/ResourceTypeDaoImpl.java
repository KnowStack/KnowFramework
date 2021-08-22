package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.resource.type.ResourceTypeQueryDTO;
import com.didiglobal.logi.security.common.po.ResourceTypePO;
import com.didiglobal.logi.security.dao.ResourceTypeDao;
import com.didiglobal.logi.security.dao.mapper.ResourceTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class ResourceTypeDaoImpl implements ResourceTypeDao {

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Override
    public List<ResourceTypePO> selectAll() {
        return resourceTypeMapper.selectList(null);
    }

    @Override
    public IPage<ResourceTypePO> selectPage(ResourceTypeQueryDTO queryDTO) {
        IPage<ResourceTypePO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<ResourceTypePO> queryWrapper = new QueryWrapper<>();
        String typeName = queryDTO.getTypeName();
        queryWrapper.like(!StringUtils.isEmpty(typeName), "type_name", typeName);
        return resourceTypeMapper.selectPage(iPage, queryWrapper);
    }

    @Override
    public ResourceTypePO selectByResourceTypeId(Integer resourceTypeId) {
        return resourceTypeMapper.selectById(resourceTypeId);
    }
}
