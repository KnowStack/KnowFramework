package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.resource.type.ResourceTypeQueryDTO;
import com.didiglobal.logi.security.common.po.ResourceTypePO;
import com.didiglobal.logi.security.common.vo.resource.ResourceTypeVO;
import com.didiglobal.logi.security.mapper.ResourceTypeMapper;
import com.didiglobal.logi.security.service.ResourceTypeService;
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
public class ResourceTypeServiceImpl implements ResourceTypeService {

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Override
    public List<ResourceTypeVO> getAllResourceTypeList() {
        List<ResourceTypePO> resourceTypeList = resourceTypeMapper.selectList(null);
        return CopyBeanUtil.copyList(resourceTypeList, ResourceTypeVO.class);
    }

    @Override
    public List<Integer> getAllResourceTypeIdList() {
        QueryWrapper<ResourceTypePO> resourceTypeWrapper = new QueryWrapper<>();
        resourceTypeWrapper.select("id");
        List<Object> resourceTypeIdList = resourceTypeMapper.selectObjs(resourceTypeWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object resourceTypeId : resourceTypeIdList) {
            result.add((Integer) resourceTypeId);
        }
        return result;
    }

    @Override
    public PagingData<ResourceTypeVO> getResourceTypePage(ResourceTypeQueryDTO queryDTO) {
        IPage<ResourceTypePO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<ResourceTypePO> queryWrapper = new QueryWrapper<>();
        String typeName = queryDTO.getTypeName();
        queryWrapper.like(!StringUtils.isEmpty(typeName), "type_name", typeName);
        resourceTypeMapper.selectPage(iPage, queryWrapper);
        List<ResourceTypeVO> list = CopyBeanUtil.copyList(iPage.getRecords(), ResourceTypeVO.class);
        return new PagingData<>(list, iPage);
    }

    @Override
    public ResourceTypeVO getResourceTypeByResourceTypeId(Integer resourceTypeId) {
        if(resourceTypeId == null) {
            return null;
        }
        ResourceTypePO resourceTypePO = resourceTypeMapper.selectById(resourceTypeId);
        return CopyBeanUtil.copy(resourceTypePO, ResourceTypeVO.class);
    }

}
