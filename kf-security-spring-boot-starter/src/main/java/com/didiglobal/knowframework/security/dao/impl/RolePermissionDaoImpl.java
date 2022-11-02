package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.entity.RolePermission;
import com.didiglobal.knowframework.security.common.po.RolePermissionPO;
import com.didiglobal.knowframework.security.dao.RolePermissionDao;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.didiglobal.knowframework.security.dao.mapper.RolePermissionMapper;
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
public class RolePermissionDaoImpl extends BaseDaoImpl<RolePermissionPO> implements RolePermissionDao {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void insertBatch(List<RolePermission> rolePermissionList) {
        if(CollectionUtils.isEmpty(rolePermissionList)) {
            return;
        }
        List<RolePermissionPO> rolePermissionPOList = CopyBeanUtil.copyList(rolePermissionList, RolePermissionPO.class);
        for(RolePermissionPO rolePermissionPO : rolePermissionPOList) {
            rolePermissionPO.setAppName( kfSecurityProper.getAppName());
            rolePermissionMapper.insert(rolePermissionPO);
        }

    }

    @Override
    public void deleteByRoleId(Integer roleId) {
        if (roleId == null) {
            return;
        }
        QueryWrapper<RolePermissionPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq( FieldConstant.ROLE_ID, roleId);
        rolePermissionMapper.delete(queryWrapper);
    }

    @Override
    public List<Integer> selectPermissionIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        List<Integer> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        return selectPermissionIdListByRoleIdList(roleIdList);
    }

    @Override
    public List<Integer> selectPermissionIdListByRoleIdList(List<Integer> roleIdList) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePermissionPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(FieldConstant.PERMISSION_ID).in(FieldConstant.ROLE_ID, roleIdList);
        List<Object> permissionIdList = rolePermissionMapper.selectObjs(queryWrapper);
        return permissionIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }
}
