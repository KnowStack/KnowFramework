package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.UserRolePO;
import com.didiglobal.logi.security.dao.UserRoleDao;
import com.didiglobal.logi.security.dao.mapper.UserRoleMapper;
import com.didiglobal.logi.security.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public List<Integer> getUserIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        return userRoleDao.selectUserIdListByRoleId(roleId);
    }

    @Override
    public List<Integer> getRoleIdListByUserId(Integer userId) {
        if(userId == null) {
            return new ArrayList<>();
        }
        return userRoleDao.selectRoleIdListByUserId(userId);
    }

    @Override
    public void updateUserRoleByUserId(Integer userId, List<Integer> roleIdList) {
        if(userId == null) {
            return;
        }

        // 删除old的全部角色用户关联信息
        userRoleDao.deleteByUserIdOrRoleId(userId, null);

        if(CollectionUtils.isEmpty(roleIdList)) {
            return;
        }

        // 插入new的角色与用户关联关系
        userRoleDao.insertBatch(getUserRoleList(true, userId, roleIdList));
    }

    @Override
    public void updateUserRoleByRoleId(Integer roleId, List<Integer> userIdList) {
        if(roleId == null) {
            return;
        }

        // 删除old的全部角色用户关联信息
        userRoleDao.deleteByUserIdOrRoleId(null, roleId);

        if(CollectionUtils.isEmpty(userIdList)) {
            return;
        }

        // 插入new的角色与用户关联关系
        userRoleDao.insertBatch(getUserRoleList(false, roleId, userIdList));
    }

    private List<UserRolePO> getUserRoleList(boolean isUserId, Integer id, List<Integer> idList) {
        List<UserRolePO> result = new ArrayList<>();
        for(Integer id2 : idList) {
            result.add(isUserId ? new UserRolePO(id, id2) : new UserRolePO(id2, id));
        }
        return result;
    }
}
