package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.resource.type.ResourceTypeQueryDTO;
import com.didiglobal.logi.security.common.po.ResourceTypePO;
import com.didiglobal.logi.security.common.vo.resource.ResourceTypeVO;
import com.didiglobal.logi.security.dao.ResourceTypeDao;
import com.didiglobal.logi.security.service.ResourceTypeService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class ResourceTypeServiceImpl implements ResourceTypeService {

    @Autowired
    private ResourceTypeDao resourceTypeDao;

    @Override
    public List<ResourceTypeVO> getAllResourceTypeList() {
        List<ResourceTypePO> resourceTypePOList = resourceTypeDao.selectAll();
        return CopyBeanUtil.copyList(resourceTypePOList, ResourceTypeVO.class);
    }

    @Override
    public List<Integer> getAllResourceTypeIdList() {
        List<ResourceTypePO> resourceTypePOList = resourceTypeDao.selectAll();
        List<Integer> result = new ArrayList<>();
        for(ResourceTypePO resourceTypePO : resourceTypePOList) {
            result.add(resourceTypePO.getId());
        }
        return result;
    }

    @Override
    public PagingData<ResourceTypeVO> getResourceTypePage(ResourceTypeQueryDTO queryDTO) {
        IPage<ResourceTypePO> iPage = resourceTypeDao.selectPage(queryDTO);
        List<ResourceTypeVO> list = CopyBeanUtil.copyList(iPage.getRecords(), ResourceTypeVO.class);
        return new PagingData<>(list, iPage);
    }

    @Override
    public ResourceTypeVO getResourceTypeByResourceTypeId(Integer resourceTypeId) {
        if(resourceTypeId == null) {
            return null;
        }
        ResourceTypePO resourceTypePO = resourceTypeDao.selectByResourceTypeId(resourceTypeId);
        return CopyBeanUtil.copy(resourceTypePO, ResourceTypeVO.class);
    }
}
