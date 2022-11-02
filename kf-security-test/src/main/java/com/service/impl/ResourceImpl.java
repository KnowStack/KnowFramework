package com.service.impl;

import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.dto.resource.ResourceDTO;
import com.didiglobal.knowframework.security.extend.ResourceExtend;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class ResourceImpl implements ResourceExtend {

    @Override
    public PagingData<ResourceDTO> getResourcePage(Integer projectId, Integer resourceTypeId,
                                                   String resourceName, int page, int size) {
        return null;
    }

    @Override
    public List<ResourceDTO> getResourceList(Integer projectId, Integer resourceTypeId) {
        return new ArrayList<>();
    }

    @Override
    public int getResourceCnt(Integer projectId, Integer resourceTypeId) {
        return -1;
    }
}
