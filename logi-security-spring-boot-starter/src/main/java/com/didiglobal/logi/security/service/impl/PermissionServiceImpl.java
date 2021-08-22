package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import com.didiglobal.logi.security.common.vo.permission.PermissionTreeVO;
import com.didiglobal.logi.security.dao.PermissionDao;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.service.PermissionService;
import com.didiglobal.logi.security.service.RolePermissionService;
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
    private PermissionDao permissionDao;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public PermissionTreeVO buildPermissionTree(Set<Integer> permissionHasSet) {
        // 获取全部权限，根据level小到大排序
        List<Permission> permissionList = permissionDao.selectAllAndAscOrderByLevel();

        // 创建一个虚拟根节点
        PermissionTreeVO root = PermissionTreeVO
                .builder().leaf(false).has(true).id(0).childList(new ArrayList<>()).build();

        // 转成树
        Map<Integer, PermissionTreeVO> parentMap = new HashMap<>(permissionList.size());
        parentMap.put(0, root);
        for(Permission permission : permissionList) {
            PermissionTreeVO permissionTreeVO = CopyBeanUtil.copy(permission, PermissionTreeVO.class);
            if(!permission.getLeaf()) {
                permissionTreeVO.setChildList(new ArrayList<>());
            }
            PermissionTreeVO parent = parentMap.get(permission.getParentId());
            if (parent == null) {
                // 如果parent为null，则需要查看下数据库权限表的数据是否有误
                // 1.可能出现了本来该是父节点的节点（有其他子节点的parent为它），但该节点parent为其他子节点的情况（数据异常）
                // 2.也可能是level填写错了（因为前面根据level大小排序）
                throw new SecurityException(ResultCode.PERMISSION_DATA_ERROR);
            }
            // 父权限拥有，子权限才肯定拥有
            permissionTreeVO.setHas(parent.getHas() && permissionHasSet.contains(permission.getId()));
            parent.getChildList().add(permissionTreeVO);
            parentMap.put(permissionTreeVO.getId(), permissionTreeVO);
        }
        return root;
    }

    @Override
    public PermissionTreeVO buildPermissionTree() {
        return buildPermissionTree(new HashSet<>());
    }

    @Override
    public PermissionTreeVO buildPermissionTreeByRoleId(Integer roleId) {
        QueryWrapper<RolePermissionPO> wrapper = new QueryWrapper<>();
        // 获取该角色拥有的全部权限id
        List<Integer> permissionIdList = rolePermissionService.getPermissionIdListByRoleId(roleId);
        Set<Integer> permissionHasSet = new HashSet<>(permissionIdList);
        return buildPermissionTree(permissionHasSet);
    }
}
