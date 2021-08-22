package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.po.UserRolePO;

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
     * @param userRolePOList 用户角色关联信息
     */
    void insertBatch(List<UserRolePO> userRolePOList);

    /**
     * 根据角色或者用户id，删除用户与角色的关系
     * @param userId 用户id
     * @param roleId 角色id
     */
    void deleteByUserIdOrRoleId(Integer userId, Integer roleId);
}
