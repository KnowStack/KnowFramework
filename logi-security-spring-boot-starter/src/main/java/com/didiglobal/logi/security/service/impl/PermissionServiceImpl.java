package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.mapper.PermissionMapper;
import com.didiglobal.logi.security.service.PermissionService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author cjm
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public PermissionVo buildPermissionTree(Set<Integer> permissionHasSet) {
        // 获取全部权限
        List<Permission> permissionList = permissionMapper.selectList(null);
        // 根据parentId小到大排序
        permissionList.sort(Comparator.comparingInt(Permission::getParentId));

        // 创建一个虚拟根节点
        PermissionVo root = PermissionVo
                .builder().isLeaf(false).isHas(true).id(0).childList(new ArrayList<>()).build();

        // 转成树
        Map<Integer, PermissionVo> parentMap = new HashMap<>();
        parentMap.put(0, root);
        for(Permission permission : permissionList) {
            PermissionVo permissionVo = CopyBeanUtil.copy(permission, PermissionVo.class);
            permissionVo.setChildList(new ArrayList<>());
            PermissionVo parent = parentMap.get(permission.getParentId());
            // 父权限拥有，子权限才肯定拥有
            permissionVo.setIsHas(parent.getIsHas() && permissionHasSet.contains(permission.getId()));
            parent.getChildList().add(permissionVo);
            parentMap.put(permissionVo.getId(), permissionVo);
        }
        return root;
    }

    @Override
    public PermissionVo buildPermissionTree() {
        return buildPermissionTree(new HashSet<>());
    }
}
