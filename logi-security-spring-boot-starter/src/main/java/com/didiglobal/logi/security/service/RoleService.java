package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.common.vo.role.*;
import com.didiglobal.logi.security.common.vo.user.UserVo;

import java.util.List;
import java.util.Set;

/**
 * @author cjm
 */
public interface RoleService {

    /**
     * 获取角色详情（主要是获取角色所拥有的权限信息）
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
    PagingData<RoleVo> getRolePage(RoleQueryVo queryVo);

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

    /**
     * 根据角色id和name获取用户list
     * @param roleId 角色id
     * @param name 用户实名或账户名
     * @return List<AssignDataVo>
     */
    List<AssignDataVo> getAssignDataByRoleId(Integer roleId, String name);
}
