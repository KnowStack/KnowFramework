package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.UserRole;
import com.didiglobal.logi.security.common.po.UserRolePO;
import com.didiglobal.logi.security.dao.UserRoleDao;
import com.didiglobal.logi.security.dao.mapper.UserRoleMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class UserRoleDaoImpl extends BaseDaoImpl<UserRolePO> implements UserRoleDao {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Integer> selectUserIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> queryWrapper = getQueryWrapper();
        queryWrapper.select("user_id").eq("role_id", roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(queryWrapper);
        return userIdList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }

    @Override
    public List<Integer> selectRoleIdListByUserId(Integer userId) {
        if(userId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> userRoleWrapper = getQueryWrapper();
        userRoleWrapper.select("role_id").eq("user_id", userId);
        List<Object> roleIdList = userRoleMapper.selectObjs(userRoleWrapper);
        return roleIdList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }

    @Override
    public void insertBatch(List<UserRole> userRoleList) {
        if(!CollectionUtils.isEmpty(userRoleList)) {
            List<UserRolePO> userRolePOList = CopyBeanUtil.copyList(userRoleList, UserRolePO.class);
            for(UserRolePO userRolePO : userRolePOList) {
                userRoleMapper.insert(userRolePO);
            }
        }
    }

    @Override
    public void deleteByUserIdOrRoleId(Integer userId, Integer roleId) {
        if(userId == null && roleId == null) {
            return;
        }
        QueryWrapper<UserRolePO> queryWrapper = getQueryWrapper();
        queryWrapper
                .eq(userId != null, "user_id", userId)
                .eq(roleId != null, "role_id", roleId);
        userRoleMapper.delete(queryWrapper);
    }

    @Override
    public int selectCountByRoleId(Integer roleId) {
        if(roleId == null) {
            return 0;
        }
        QueryWrapper<UserRolePO> queryWrapper = getQueryWrapper();
        queryWrapper.eq("role_id", roleId);
        return userRoleMapper.selectCount(queryWrapper);
    }
}
