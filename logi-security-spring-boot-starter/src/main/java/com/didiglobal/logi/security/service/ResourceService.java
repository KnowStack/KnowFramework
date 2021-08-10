package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.resource.*;

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
    PagingData<MByRVo> getManageByResourcePage(MByRQueryVo queryVo);

    /**
     * 资源权限管理>按用户管理的列表信息
     * 获取分页列表信息
     * @param queryVo 查询条件
     * @return PagingData<ManageByUserVo>
     */
    PagingData<MByUVo> getManageByUserPage(MByUQueryVo queryVo);

    /**
     * 分配资源的权限给用户（N资源权限分配给某用户）
     * @param assignToOneUserVo 分配信息
     */
    void assignResourcePermission(AssignToOneUserVo assignToOneUserVo);

    /**
     * 分配资源的权限给用户（某资源权限分配N个用户）
     * @param assignToManyUserVo 分配信息
     */
    void assignResourcePermission(AssignToManyUserVo assignToManyUserVo);

    /**
     * 获取所有资源类型list
     * @return List<ResourceTypeVo>
     */
    List<ResourceTypeVo> getResourceTypeList();

    /**
     * 获取所有资源类型list
     * @param projectId 项目id
     * @param resourceTypeId 资源类别id
     * @return List<ResourceVo>
     */
    List<MByUDataQueryVo> getResourceList(Integer projectId, Integer resourceTypeId);

    /**
     * 资源权限管理/按用户管理/分配资源/数据列表的信息
     * @param queryVo 查询条件
     * @return 数据列表信息
     */
    List<MByUDataVo> getManagerByUserDataList(MByUDataQueryVo queryVo);
}
