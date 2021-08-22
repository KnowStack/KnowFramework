package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.po.PermissionPO;
import com.didiglobal.logi.security.dao.PermissionDao;
import com.didiglobal.logi.security.dao.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class PermissionDaoImpl implements PermissionDao {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionPO> selectAllAndAscOrderByLevel() {
        QueryWrapper<PermissionPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("level");
        return permissionMapper.selectList(queryWrapper);
    }
}
