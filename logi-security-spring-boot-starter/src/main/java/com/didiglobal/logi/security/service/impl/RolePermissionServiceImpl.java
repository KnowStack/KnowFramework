package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import com.didiglobal.logi.security.mapper.RolePermissionMapper;
import com.didiglobal.logi.security.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void saveRolePermission(Integer roleId, List<Integer> permissionIdList) {
        if(roleId == null || CollectionUtils.isEmpty(permissionIdList)) {
            return;
        }
        List<RolePermissionPO> rolePermissionList = getRolePermissionList(roleId, permissionIdList);
        // 插入新的关联信息
        rolePermissionMapper.insertBatchSomeColumn(rolePermissionList);
    }

    @Override
    public void updateRolePermission(Integer roleId, List<Integer> permissionIdList) {
        // 先删除old的关联信息
        deleteRolePermissionByRoleId(roleId);
        // 插入新的关联信息
        saveRolePermission(roleId, permissionIdList);
    }

    @Override
    public void deleteRolePermissionByRoleId(Integer roleId) {
        if(roleId == null) {
            return;
        }
        QueryWrapper<RolePermissionPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(queryWrapper);
    }

    @Override
    public List<Integer> getPermissionIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePermissionPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("permission_id").eq("role_id", roleId);
        List<Object> permissionIdList = rolePermissionMapper.selectObjs(queryWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object permissionId : permissionIdList) {
            result.add((Integer) permissionId);
        }
        return result;
    }

    /**
     * 用于构建可以直接插入角色与权限中间表的数据
     * @param roleId 角色Id
     * @param permissionIdList 权限idList
     * @return List<RolePermissionPO>
     */
    private List<RolePermissionPO> getRolePermissionList(Integer roleId, List<Integer> permissionIdList) {
        List<RolePermissionPO> rolePermissionPOList = new ArrayList<>();
        for(Integer permissionId : permissionIdList) {
            RolePermissionPO rolePermissionPO = new RolePermissionPO();
            rolePermissionPO.setRoleId(roleId);
            rolePermissionPO.setPermissionId(permissionId);
            rolePermissionPOList.add(rolePermissionPO);
        }
        return rolePermissionPOList;
    }
}
