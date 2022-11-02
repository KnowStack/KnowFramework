package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.po.PermissionPO;
import com.didiglobal.knowframework.security.dao.PermissionDao;
import com.didiglobal.knowframework.security.common.entity.Permission;
import com.didiglobal.knowframework.security.dao.mapper.PermissionMapper;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class PermissionDaoImpl extends BaseDaoImpl<PermissionPO> implements PermissionDao {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> selectAllAndAscOrderByLevel() {
        QueryWrapper<PermissionPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.orderByAsc( FieldConstant.LEVEL);
        return CopyBeanUtil.copyList(permissionMapper.selectList(queryWrapper), Permission.class);
    }

    @Override
    public void insertBatch(List<Permission> permissionList) {
        if(CollectionUtils.isEmpty(permissionList)) {
            return;
        }
        List<PermissionPO> permissionPOList = CopyBeanUtil.copyList(permissionList, PermissionPO.class);
        for(PermissionPO permissionPO : permissionPOList) {
            permissionPO.setAppName( kfSecurityProper.getAppName());
            permissionMapper.insert(permissionPO);
        }

    }
}
