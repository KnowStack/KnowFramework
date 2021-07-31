package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.resource.ResourceQueryVo;
import com.didiglobal.logi.security.common.vo.resource.ResourceTypeVo;
import com.didiglobal.logi.security.common.vo.resource.ManageByResourceVo;

import java.util.List;

/**
 * @author cjm
 */
public interface ResourceService {

    /**
     * 资源权限管理>按资源管理的列表信息
     * 获取分页列表信息
     * @param queryVo 查询条件
     * @return PagingData<ManageByResourceVo>
     */
    PagingData<ManageByResourceVo> getPageManageByResource(ResourceQueryVo queryVo);

    /**
     * 获取所有资源类型list
     * @return List<ResourceTypeVo>
     */
    List<ResourceTypeVo> getResourceTypeList();
}
