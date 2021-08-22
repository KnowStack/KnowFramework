package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.vo.permission.PermissionTreeVO;

import java.util.List;
import java.util.Set;

/**
 * @author cjm
 */
public interface PermissionService {

    /**
     * 构建权限树，并标注拥有的权限
     * @param permissionHasList 拥有的权限（只包含权限id）
     * @return PermissionVo 权限树
     */
    PermissionTreeVO buildPermissionTreeWithHas(List<Integer> permissionHasList);

    /**
     * 构建权限树（返回所有权限点）
     * @return PermissionVo 权限树
     */
    PermissionTreeVO buildPermissionTree();

    /**
     * 根据角色id构建权限树（返回所有权限点）
     * @param roleId 角色id
     * @return PermissionVo 权限树
     */
    PermissionTreeVO buildPermissionTreeByRoleId(Integer roleId);
}
