package com.didiglobal.logi.security.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.po.RolePO;

import java.util.List;

/**
 * @author cjm
 */
public interface RoleDao {

    /**
     * 根据角色id，获取角色信息
     * @param roleId 角色id
     * @return 角色信息
     */
    RolePO selectByRoleId(Integer roleId);

    /**
     * 分页查询
     * @param queryDTO 查询条件
     * @return 角色信息page
     */
    IPage<RolePO> selectPage(RoleQueryDTO queryDTO);

    /**
     * 新增角色
     * @param rolePO 角色信息
     */
    void insert(RolePO rolePO);

    /**
     * 删除角色
     * @param roleId 角色id
     */
    void deleteByRoleId(Integer roleId);

    /**
     * 更新角色
     * @param rolePO 角色信息
     */
    void update(RolePO rolePO);

    /**
     * 获取角色简要信息
     * @param roleName 角色名
     * @return 角色简要信息List
     */
    List<RolePO> selectBriefListByRoleNameAndDescOrderByCreateTime(String roleName);

    /**
     * 获取全部角色简要信息
     * @return 角色简要信息list
     */
    List<RolePO> selectBriefList();

    /**
     * 根据角色idList获取角色简要信息
     * @param roleIdList 角色idList
     * @return List<RolePO>
     */
    List<RolePO> selectBriefListByRoleIdList(List<Integer> roleIdList);

    /**
     * 根据角色名查询符合数据数
     * 如果是更新操作，则判断角色名重复的时候要排除old角色id信息
     * @param roleName 角色名
     * @param roleId 角色id
     * @return 角色数
     */
    int selectCountByRoleNameAndNotRoleId(String roleName, Integer roleId);
}
