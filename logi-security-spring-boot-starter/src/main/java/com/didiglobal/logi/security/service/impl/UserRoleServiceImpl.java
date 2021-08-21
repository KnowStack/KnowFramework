package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.po.UserRolePO;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.UserRoleMapper;
import com.didiglobal.logi.security.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Integer> getUserIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.select("user_id").eq("role_id", roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(userRoleWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object userId : userIdList) {
            result.add((Integer) userId);
        }
        return result;
    }

    @Override
    public List<Integer> getRoleIdListByUserId(Integer userId) {
        if(userId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.select("role_id").eq("user_id", userId);
        List<Object> roleIdList = userRoleMapper.selectObjs(userRoleWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object roleId : roleIdList) {
            result.add((Integer) roleId);
        }
        return result;
    }

    @Override
    public void updateUserRoleByUserId(Integer userId, List<Integer> roleIdList) {
        if(userId == null) {
            return;
        }

        // 删除old的全部角色用户关联信息
        deleteUserRoleByUserIdOrRoleId(userId, null);

        if(CollectionUtils.isEmpty(roleIdList)) {
            return;
        }

        // 插入new的角色与用户关联关系
        List<UserRolePO> userRolePOList = getUserRoleList(true, userId, roleIdList);
        userRoleMapper.insertBatchSomeColumn(userRolePOList);
    }

    @Override
    public void updateUserRoleByRoleId(Integer roleId, List<Integer> userIdList) {
        if(roleId == null) {
            return;
        }

        // 删除old的全部角色用户关联信息
        deleteUserRoleByUserIdOrRoleId(null, roleId);

        if(CollectionUtils.isEmpty(userIdList)) {
            return;
        }

        // 插入new的角色与用户关联关系
        List<UserRolePO> userRolePOList = getUserRoleList(false, roleId, userIdList);
        userRoleMapper.insertBatchSomeColumn(userRolePOList);
    }

    private List<UserRolePO> getUserRoleList(boolean isUserId, Integer id, List<Integer> idList) {
        List<UserRolePO> result = new ArrayList<>();
        for(Integer id2 : idList) {
            result.add(isUserId ? new UserRolePO(id, id2) : new UserRolePO(id2, id));
        }
        return result;
    }

    /**
     * 根据角色或者用户id，删除用户与角色的关系
     * @param userId 用户id
     * @param roleId 角色id
     */
    private void deleteUserRoleByUserIdOrRoleId(Integer userId, Integer roleId) {
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper
                .eq(userId != null, "user_id", userId)
                .eq(roleId != null, "role_id", roleId);
        userRoleMapper.delete(userRoleWrapper);
    }
}
