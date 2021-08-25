package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.RolePermission;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import com.didiglobal.logi.security.dao.RolePermissionDao;
import com.didiglobal.logi.security.dao.mapper.RolePermissionMapper;
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
public class RolePermissionDaoImpl extends BaseDaoImpl<RolePermissionPO> implements RolePermissionDao {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void insertBatch(List<RolePermission> rolePermissionList) {
        if(!CollectionUtils.isEmpty(rolePermissionList)) {
            rolePermissionMapper.insertBatchSomeColumn(CopyBeanUtil.copyList(rolePermissionList, RolePermissionPO.class));
        }
    }

    @Override
    public void deleteByRoleId(Integer roleId) {
        if (roleId == null) {
            return;
        }
        QueryWrapper<RolePermissionPO> queryWrapper = getQueryWrapper();
        queryWrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(queryWrapper);
    }

    @Override
    public List<Integer> selectPermissionIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        List<Integer> roleIdList = new ArrayList<Integer>(){{ add(roleId); }};
        return selectPermissionIdListByRoleIdList(roleIdList);
    }

    @Override
    public List<Integer> selectPermissionIdListByRoleIdList(List<Integer> roleIdList) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePermissionPO> queryWrapper = getQueryWrapper();
        queryWrapper.select("permission_id").in("role_id", roleIdList);
        List<Object> permissionIdList = rolePermissionMapper.selectObjs(queryWrapper);
        return permissionIdList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }
}
