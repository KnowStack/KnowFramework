package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.po.RolePermissionPO;

import java.util.List;

/**
 * @author cjm
 */
public interface RolePermissionDao {

    /**
     * 批量插入
     * @param rolePermissionPOList 角色与权限关联信息
     */
    void insertBatch(List<RolePermissionPO> rolePermissionPOList);

    /**
     * 根据角色id，删除角色权限关联信息
     * @param roleId 角色id
     */
    void deleteByRoleId(Integer roleId);

    /**
     * 获取角色拥有的权限idList
     * @param roleId 角色id
     * @return 权限idList
     */
    List<Integer> selectPermissionIdListByRoleId(Integer roleId);
}
