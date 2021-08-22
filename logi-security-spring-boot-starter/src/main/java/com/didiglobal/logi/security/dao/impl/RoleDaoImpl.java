package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.po.RolePO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.dao.RoleDao;
import com.didiglobal.logi.security.dao.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Component
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public RolePO selectByRoleId(Integer roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    public IPage<RolePO> selectPage(RoleQueryDTO queryDTO) {
        IPage<RolePO> rolePage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(queryDTO.getRoleCode())) {
            roleWrapper.eq("role_code", queryDTO.getRoleCode());
        } else {
            roleWrapper
                    .like(!StringUtils.isEmpty(queryDTO.getRoleName()), "role_name", queryDTO.getRoleName())
                    .like(!StringUtils.isEmpty(queryDTO.getDescription()), "description", queryDTO.getDescription());
        }
        return roleMapper.selectPage(rolePage, roleWrapper);
    }

    @Override
    public void insert(RolePO rolePO) {
        roleMapper.insert(rolePO);
    }

    @Override
    public void deleteByRoleId(Integer roleId) {
        roleMapper.deleteById(roleId);
    }

    @Override
    public void update(RolePO rolePO) {
        roleMapper.updateById(rolePO);
    }

    @Override
    public List<RolePO> selectBriefListByRoleNameAndDescOrderByCreateTime(String roleName) {
        QueryWrapper<RolePO> queryWrapper = wrapBriefQuery();
        queryWrapper
                .like(!StringUtils.isEmpty(roleName), "role_name", roleName)
                // 据角色添加时间排序（倒序）
                .orderByDesc("create_time");
        return roleMapper.selectList(queryWrapper);
    }

    @Override
    public List<RolePO> selectBriefList() {
        return roleMapper.selectList(wrapBriefQuery());
    }

    @Override
    public List<RolePO> selectBriefListByRoleIdList(List<Integer> roleIdList) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePO> queryWrapper = wrapBriefQuery();
        queryWrapper.in("id", roleIdList);
        return roleMapper.selectList(queryWrapper);
    }

    @Override
    public int selectCountByRoleNameAndNotRoleId(String roleName, Integer roleId) {
        QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("role_name", roleName)
                .ne(roleId != null, "id", roleId);
        return roleMapper.selectCount(queryWrapper);
    }

    private QueryWrapper<RolePO> wrapBriefQuery() {
        QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "role_name");
        return queryWrapper;
    }
}
