package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import com.didiglobal.logi.security.dao.RolePermissionDao;
import com.didiglobal.logi.security.dao.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class RolePermissionDaoImpl implements RolePermissionDao {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void insertBatch(List<RolePermissionPO> rolePermissionPOList) {
        if(!CollectionUtils.isEmpty(rolePermissionPOList)) {
            rolePermissionMapper.insertBatchSomeColumn(rolePermissionPOList);
        }
    }

    @Override
    public void deleteByRoleId(Integer roleId) {
        QueryWrapper<RolePermissionPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(queryWrapper);
    }

    @Override
    public List<Integer> selectPermissionIdListByRoleId(Integer roleId) {
        QueryWrapper<RolePermissionPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<RolePermissionPO> rolePermissionPOList = rolePermissionMapper.selectList(queryWrapper);
        List<Integer> result = new ArrayList<>();
        for(RolePermissionPO rolePermissionPO : rolePermissionPOList) {
            result.add(rolePermissionPO.getPermissionId());
        }
        return result;
    }
}
