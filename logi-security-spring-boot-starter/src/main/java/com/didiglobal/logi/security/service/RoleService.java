package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.role.AssignDataDTO;
import com.didiglobal.logi.security.common.dto.role.RoleAssignDTO;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.dto.role.RoleSaveDTO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.role.RoleDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;

import java.util.List;

/**
 * @author cjm
 */
public interface RoleService {

    /**
     * 获取角色详情（主要是获取角色所拥有的权限信息）
     * @param id 角色id
     * @return RoleVo 角色信息
     */
    RoleVO getDetailById(Integer id);

    /**
     * 分页获取角色列表
     *
     * @param queryVo 查询角色列表条件
     * @return 角色列表
     */
    PagingData<RoleVO> getRolePage(RoleQueryDTO queryVo);

    /**
     * 保存角色
     * @param roleSaveDTO 角色信息
     */
    void createRole(RoleSaveDTO roleSaveDTO);

    /**
     * 删除角色
     * @param id 角色id
     */
    void deleteRoleById(Integer id);

    /**
     * 更新角色信息
     * @param roleSaveDTO 角色信息
     */
    void updateRoleById(RoleSaveDTO roleSaveDTO);

    /**
     * 分配角色给用户
     * @param roleAssignDTO 分配信息
     */
    void assignRoles(RoleAssignDTO roleAssignDTO);

    /**
     * 根据角色id和name获取用户list
     * @param roleId 角色id
     * @param name 用户实名或账户名
     * @return List<AssignDataVo>
     */
    List<AssignDataDTO> getAssignDataByRoleId(Integer roleId, String name);

    /**
     * 根据角色名模糊查询
     * @param roleName 角色名
     * @return List<RoleBriefVO> 角色简要信息list
     */
    List<RoleBriefVO> listByRoleName(String roleName);

    /**
     * 判断该角色是否已经分配给用户，如有分配给用户，则返回用户名list
     * @param roleId 角色id
     * @return RoleDeleteCheckVO 检查结果
     */
    RoleDeleteCheckVO checkBeforeDelete(Integer roleId);
}
