package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.UserRolePO;
import com.didiglobal.logi.security.dao.UserRoleDao;
import com.didiglobal.logi.security.dao.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class UserRoleDaoImpl implements UserRoleDao {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Integer> selectUserIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id").eq("role_id", roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(queryWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object userId : userIdList) {
            result.add((Integer) userId);
        }
        return result;
    }

    @Override
    public List<Integer> selectRoleIdListByUserId(Integer userId) {
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
    public void insertBatch(List<UserRolePO> userRolePOList) {
        if(!CollectionUtils.isEmpty(userRolePOList)) {
            userRoleMapper.insertBatchSomeColumn(userRolePOList);
        }
    }

    @Override
    public void deleteByUserIdOrRoleId(Integer userId, Integer roleId) {
        if(userId == null && roleId == null) {
            return;
        }
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper
                .eq(userId != null, "user_id", userId)
                .eq(roleId != null, "role_id", roleId);
        userRoleMapper.delete(userRoleWrapper);
    }
}
