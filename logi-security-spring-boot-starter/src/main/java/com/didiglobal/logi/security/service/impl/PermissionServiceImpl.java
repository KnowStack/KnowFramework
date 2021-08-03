package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.exception.SecurityException;
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
        // 获取全部权限（为了效率就先全部读出来）
        List<Permission> permissionList = permissionMapper.selectList(null);
        // 根据level小到大排序（保证树是层序遍历的）
        permissionList.sort(Comparator.comparingInt(Permission::getLevel));

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
            if (parent == null) {
                // 如果parent为null，则需要查看下数据库权限表的数据是否有误
                // 1.可能出现了本来该是父节点的节点（有其他子节点的parent为它），但该节点parent为其他子节点的情况（数据异常）
                // 2.也可能是level填写错了（因为前面根据level大小排序）
                throw new SecurityException(ResultCode.PERMISSION_DATA_ERROR);
            }
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
