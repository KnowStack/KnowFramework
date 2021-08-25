package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.entity.role.Role;
import com.didiglobal.logi.security.common.entity.role.RoleBrief;
import com.didiglobal.logi.security.common.po.RolePO;
import com.didiglobal.logi.security.dao.RoleDao;
import com.didiglobal.logi.security.dao.mapper.RoleMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
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
public class RoleDaoImpl extends BaseDaoImpl<RolePO> implements RoleDao {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role selectByRoleId(Integer roleId) {
        QueryWrapper<RolePO> queryWrapper = getQueryWrapper();
        queryWrapper.eq("id", roleId);
        return CopyBeanUtil.copy(roleMapper.selectOne(queryWrapper), Role.class);
    }

    @Override
    public IPage<Role> selectPage(RoleQueryDTO queryDTO) {
        IPage<RolePO> iPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<RolePO> roleWrapper = getQueryWrapper();
        if(!StringUtils.isEmpty(queryDTO.getRoleCode())) {
            roleWrapper.eq("role_code", queryDTO.getRoleCode());
        } else {
            roleWrapper
                    .like(!StringUtils.isEmpty(queryDTO.getRoleName()), "role_name", queryDTO.getRoleName())
                    .like(!StringUtils.isEmpty(queryDTO.getDescription()), "description", queryDTO.getDescription());
        }
        roleMapper.selectPage(iPage, roleWrapper);
        return CopyBeanUtil.copyPage(iPage, Role.class);
    }

    @Override
    public void insert(Role role) {
        RolePO rolePO = CopyBeanUtil.copy(role, RolePO.class);
        roleMapper.insert(rolePO);
        role.setId(rolePO.getId());
    }

    @Override
    public void deleteByRoleId(Integer roleId) {
        roleMapper.deleteById(roleId);
    }

    @Override
    public void update(Role role) {
        roleMapper.updateById(CopyBeanUtil.copy(role, RolePO.class));
    }

    @Override
    public List<RoleBrief> selectBriefListByRoleNameAndDescOrderByCreateTime(String roleName) {
        QueryWrapper<RolePO> queryWrapper = wrapBriefQuery();
        queryWrapper
                .like(!StringUtils.isEmpty(roleName), "role_name", roleName)
                // 据角色添加时间排序（倒序）
                .orderByDesc("create_time");
        return CopyBeanUtil.copyList(roleMapper.selectList(queryWrapper), RoleBrief.class);
    }

    @Override
    public List<RoleBrief> selectAllBrief() {
        return CopyBeanUtil.copyList(roleMapper.selectList(wrapBriefQuery()), RoleBrief.class);
    }

    @Override
    public List<RoleBrief> selectBriefListByRoleIdList(List<Integer> roleIdList) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePO> queryWrapper = wrapBriefQuery();
        queryWrapper.in("id", roleIdList);
        return CopyBeanUtil.copyList(roleMapper.selectList(queryWrapper), RoleBrief.class);
    }

    @Override
    public int selectCountByRoleNameAndNotRoleId(String roleName, Integer roleId) {
        QueryWrapper<RolePO> queryWrapper = getQueryWrapper();
        queryWrapper
                .eq("role_name", roleName)
                .ne(roleId != null, "id", roleId);
        return roleMapper.selectCount(queryWrapper);
    }

    private QueryWrapper<RolePO> wrapBriefQuery() {
        QueryWrapper<RolePO> queryWrapper = getQueryWrapper();
        queryWrapper.select("id", "role_name");
        return queryWrapper;
    }
}
