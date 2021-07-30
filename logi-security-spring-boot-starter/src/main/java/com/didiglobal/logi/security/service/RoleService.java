package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.common.vo.role.RoleAssignVo;
import com.didiglobal.logi.security.common.vo.role.RoleQueryVo;
import com.didiglobal.logi.security.common.vo.role.RoleSaveVo;
import com.didiglobal.logi.security.common.vo.role.RoleVo;

import java.util.List;
import java.util.Set;

/**
 * @author cjm
 */
public interface RoleService {

    /**
     * 获取角色详情，通过角色id或角色编号
     * @param id 角色id
     * @return RoleVo 角色信息
     */
    RoleVo getDetailById(Integer id);

    /**
     * 分页获取角色列表
     *
     * @param queryVo 查询角色列表条件
     * @return 角色列表
     */
    IPage<RoleVo> getPageRole(RoleQueryVo queryVo);

    /**
     * 保存角色
     * @param roleSaveVo 角色信息
     */
    void createRole(RoleSaveVo roleSaveVo);

    /**
     * 删除角色
     * @param id 角色id
     */
    void deleteRoleById(Integer id);

    /**
     * 更新角色信息
     * @param roleSaveVo 角色信息
     */
    void updateRoleById(RoleSaveVo roleSaveVo);

    /**
     * 分配角色给用户
     * @param roleAssignVo 分配信息
     */
    void assignRoles(RoleAssignVo roleAssignVo);
}
