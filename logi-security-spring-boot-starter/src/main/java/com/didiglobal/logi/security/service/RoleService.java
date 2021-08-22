package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.role.AssignInfoVO;
import com.didiglobal.logi.security.common.dto.role.RoleAssignDTO;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.dto.role.RoleSaveDTO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.role.RoleDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import com.didiglobal.logi.security.exception.SecurityException;

import java.util.List;

/**
 * @author cjm
 */
public interface RoleService {

    /**
     * 获取角色详情（主要是获取角色所拥有的权限信息）
     * @param roleId 角色id
     * @return RoleVo 角色信息
     */
    RoleVO getRoleDetailByRoleId(Integer roleId);

    /**
     * 分页获取角色列表
     *
     * @param queryDTO 查询角色列表条件
     * @return 角色列表
     */
    PagingData<RoleVO> getRolePage(RoleQueryDTO queryDTO);

    /**
     * 保存角色
     * @param userId 角色创建者的用户id
     * @param saveDTO 角色信息
     * @throws SecurityException 参数检查错误信息
     */
    void createRoleWithUserId(Integer userId, RoleSaveDTO saveDTO) throws SecurityException;

    /**
     * 删除角色
     * @param id 角色id
     */
    void deleteRoleByRoleId(Integer id);

    /**
     * 更新角色信息
     * @param userId 角色更新者的用户id
     * @param saveDTO 角色信息
     */
    void updateRoleWithUserId(Integer userId, RoleSaveDTO saveDTO);

    /**
     * 分配角色给用户
     * @param assignDTO 分配信息
     * @throws SecurityException 角色分配flag不可为空
     */
    void assignRoles(RoleAssignDTO assignDTO) throws SecurityException;

    /**
     * 根据角色id，获取分配信息
     * @param roleId 角色id
     * @param name 用户实名或账户名
     * @return List<AssignDataVo> 分配信息
     */
    List<AssignInfoVO> getAssignInfoByRoleId(Integer roleId, String name);

    /**
     * 根据角色名模糊查询
     * @param roleName 角色名
     * @return List<RoleBriefVO> 角色简要信息list
     */
    List<RoleBriefVO> getRoleBriefListByRoleName(String roleName);

    /**
     * 判断该角色是否已经分配给用户，如有分配给用户，则返回用户名list
     * @param roleId 角色id
     * @return RoleDeleteCheckVO 检查结果
     */
    RoleDeleteCheckVO checkBeforeDelete(Integer roleId);

    /**
     * 获取所有角色的简要信息
     * @return List<RoleBriefVO> 角色简要信息
     */
    List<RoleBriefVO> getAllRoleBriefList();

    /**
     * 根据用户id获取用户拥有的角色信息
     * @param userId 用户id
     * @return List<RoleBriefVO> 角色简要信息
     */
    List<RoleBriefVO> getRoleBriefListByUserId(Integer userId);
}
