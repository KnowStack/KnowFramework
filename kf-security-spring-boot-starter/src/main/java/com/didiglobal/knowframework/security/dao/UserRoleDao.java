package com.didiglobal.knowframework.security.dao;

import com.didiglobal.knowframework.security.common.entity.UserRole;
import com.didiglobal.knowframework.security.common.po.UserRolePO;

import java.util.List;

/**
 * @author cjm
 */
public interface UserRoleDao {

    /**
     * 根据角色id获取用户idList
     * @param roleId 角色id
     * @return 用户idList
     */
    List<Integer> selectUserIdListByRoleId(Integer roleId);

    /**
     * 根据用户id获取角色idList
     * @param userId 用户id
     * @return 角色idList
     */
    List<Integer> selectRoleIdListByUserId(Integer userId);

    /**
     * 批量插入
     * @param userRoleList 用户角色关联信息
     */
    void insertBatch(List<UserRole> userRoleList);

    /**
     * 根据角色或者用户id，删除用户与角色的关系
     * @param userId 用户id
     * @param roleId 角色id
     */
    int deleteByUserIdOrRoleId(Integer userId, Integer roleId);

    /**
     * 根据角色id获取授予用户数
     * @param roleId 角色id
     * @return 角色授予用户数
     */
    int selectCountByRoleId(Integer roleId);

    /**
     * 选择角色 ID 在给定角色 ID 列表中的所有用户角色。
     *
     * @param roleIds 角色ID列表
     * @return UserRole 对象的列表。
     */
    List<UserRolePO> selectByRoleIds(List<Integer> roleIds);

    /**
     * 通过用户id获取角色id列表
     *
     * @param userIds 用户ID列表
     * @return 列表<UserRolePO>
     */
    List<UserRolePO> getRoleIdListByUserIds(List<Integer> userIds);
}
