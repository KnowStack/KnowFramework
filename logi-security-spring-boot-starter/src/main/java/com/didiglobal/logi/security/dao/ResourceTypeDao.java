package com.didiglobal.logi.security.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.dto.resource.type.ResourceTypeQueryDTO;
import com.didiglobal.logi.security.common.po.ResourceTypePO;

import java.util.List;

/**
 * @author cjm
 */
public interface ResourceTypeDao {

    /**
     * 获取全部资源类别
     * @return 资源类别List
     */
    List<ResourceTypePO> selectAll();

    /**
     * 分页获取资源类别
     * @param queryDTO 查询条件
     * @return 资源类别page
     */
    IPage<ResourceTypePO> selectPage(ResourceTypeQueryDTO queryDTO);

    /**
     * 根据资源类别id，查询资源类别信息
     * @param resourceTypeId 资源类别id
     * @return 资源类别信息
     */
    ResourceTypePO selectByResourceTypeId(Integer resourceTypeId);
}
