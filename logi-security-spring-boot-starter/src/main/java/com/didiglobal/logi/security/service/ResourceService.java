package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.resource.*;
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
    PagingData<MByRVO> getManageByResourcePage(MByRQueryDTO queryVo);

    /**
     * 资源权限管理>按用户管理的列表信息
     * 获取分页列表信息
     * @param queryVo 查询条件
     * @return PagingData<ManageByUserVo>
     */
    PagingData<MByUVO> getManageByUserPage(MByUQueryDTO queryVo);

    /**
     * 分配资源的权限给用户（N资源权限分配给某用户）
     * @param assignToOneUserDTO 分配信息
     */
    void assignResourcePermission(AssignToOneUserDTO assignToOneUserDTO);

    /**
     * 分配资源的权限给用户（某资源权限分配N个用户）
     * @param assignToManyUserDTO 分配信息
     */
    void assignResourcePermission(AssignToManyUserDTO assignToManyUserDTO);

    /**
     * 批量分配资源的权限给用户
     * 按资源管理下的批量分配用户：分配之前先删除N资源先前已分配的用户
     * 按用户管理下的批量分配资源：分配之前先删除N用户已拥有的资源权限
     * @param batchAssignDTO 分配信息
     */
    void batchAssignResourcePermission(BatchAssignDTO batchAssignDTO);

    /**
     * 获取所有资源类型list
     * @return List<ResourceTypeVo>
     */
    List<ResourceTypeVO> getResourceTypeList();

    /**
     * 资源权限管理/按用户管理/分配资源/数据列表的信息
     * @param queryVo 查询条件
     * @return 数据列表信息
     */
    List<MByUDataVO> getManagerByUserDataList(MByUDataQueryDTO queryVo);

    /**
     * 资源权限管理/按资源管理/分配用户/数据列表的信息
     * @param queryVo 查询条件
     * @return 数据列表信息
     */
    List<MByRDataVO> getManagerByResourceDataList(MByRDataQueryDTO queryVo);

    /**
     * 获取资源查看权限控制状态
     * @return true开启，false关闭
     */
    boolean getViewPermissionControlStatus();

    /**
     * 调用该接口则资源查看权限控制状态被反转
     */
    void changeResourceViewControlStatus();
}
