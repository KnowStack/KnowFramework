package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.role.AssignDataVo;
import com.didiglobal.logi.security.common.vo.user.UserQueryVo;
import com.didiglobal.logi.security.common.vo.user.UserVo;

import java.util.List;

public interface UserService {

    /**
     * 分页获取用户信息
     *
     * @param queryVo 条件信息
     * @return 用户信息list
     */
    PagingData<UserVo> getUserPage(UserQueryVo queryVo);

    /**
     * 获取用户详情（主要是获取用户所拥有的权限信息）
     *
     * @param userId 用户id
     * @return 用户详情
     */
    UserVo getDetailById(Integer userId);

    /**
     * 根据部门id获取用户list（获取该部门下所有的用户，包括各种子部门）
     * @param deptId 部门id，如果为null，表示无部门用户
     * @return 用户list
     */
    List<UserVo> getListByDeptId(Integer deptId);

    /**
     * 根据用户id和roleName获取角色list
     * @param userId 用户id
     * @param roleName 角色名
     * @return List<AssignDataVo>
     */
    List<AssignDataVo> getAssignDataByUserId(Integer userId, String roleName);

    /**
     * 根据角色id获取用户list
     * @param roleId 角色Id
     * @return List<UserVo>
     */
    List<UserVo> getListByRoleId(Integer roleId);
}
