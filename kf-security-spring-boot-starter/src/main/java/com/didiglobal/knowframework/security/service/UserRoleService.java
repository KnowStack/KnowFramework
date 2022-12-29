package com.didiglobal.knowframework.security.service;

import com.didiglobal.knowframework.security.common.entity.UserRole;

import java.util.List;

/**
 * @author cjm
 */
public interface UserRoleService {

    /**
     * 根据角色id，获取用户idList
     * @param roleId 角色id
     * @return 用户idList
     */
    List<Integer> getUserIdListByRoleId(Integer roleId);

    /**
     * 根据用户id，获取角色idList
     * @param userId 用户id
     * @return 角色idList
     */
    List<Integer> getRoleIdListByUserId(Integer userId);

    /**
     * 根据用户id，更新用户与角色的关联信息
     * @param userId 用户id
     * @param roleIdList 角色idList
     */
    void updateUserRoleByUserId(Integer userId, List<Integer> roleIdList);

    /**
     * 根据角色id，更新用户与角色的关联信息
     * @param roleId 角色id
     * @param userIdList 用户idList
     */
    void updateUserRoleByRoleId(Integer roleId, List<Integer> userIdList);

    /**
     * 根据角色id获取授予用户数
     * @param roleId 角色id
     * @return 角色授予用户数
     */
    int getUserRoleCountByRoleId(Integer roleId);

    /**
     * 根据角色或者用户id，删除用户与角色的关系
     * @param userId 用户id
     * @param roleId 角色id
     */
    int deleteByUserIdOrRoleId(Integer userId, Integer roleId);
    /**
     * 获取角色 ID 在给定角色 ID 列表中的所有用户角色。
     *
     * @param roleIds 角色 ID 列表。
     * @return UserRole 对象的列表。
     */
    List<UserRole> getByRoleIds(List<Integer> roleIds);
    /**
     * 通过用户 ID 列表获取用户角色列表
     *
     * @param userId 用户编号
     * @return 列表<用户角色>
     */
    List<UserRole> getRoleIdListByUserIds(List<Integer> userId);
}
