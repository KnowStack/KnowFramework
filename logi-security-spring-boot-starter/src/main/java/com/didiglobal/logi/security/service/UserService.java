package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.role.AssignDataDTO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;

import java.util.List;

public interface UserService {

    /**
     * 分页获取用户信息
     *
     * @param queryVo 条件信息
     * @return 用户信息list
     */
    PagingData<UserVO> getUserPage(UserQueryDTO queryVo);

    /**
     * 获取用户详情（主要是获取用户所拥有的权限信息）
     *
     * @param userId 用户id
     * @return 用户详情
     */
    UserVO getDetailById(Integer userId);

    /**
     * 根据部门id获取用户list（获取该部门下所有的用户，包括各种子部门）
     * @param deptId 部门id，如果为null，表示无部门用户
     * @return List<UserBriefVO> 用户简要信息list
     */
    List<UserBriefVO> getListByDeptId(Integer deptId);

    /**
     * 根据用户id和roleName获取角色list
     * @param userId 用户id
     * @param roleName 角色名
     * @return List<AssignDataVo>
     */
    List<AssignDataDTO> getAssignDataByUserId(Integer userId, String roleName);

    /**
     * 根据角色id获取用户list
     * @param roleId 角色Id
     * @return List<UserBriefVO> 用户简要信息list
     */
    List<UserBriefVO> getListByRoleId(Integer roleId);

    /**
     * 会分别以账户名和实名去模糊查询，返回两者的并集
     * 创建项目，添加项目负责人的时候用到
     * @param name 账户名或实名
     * @return List<UserBriefVO> 用户简要信息list
     */
    List<UserBriefVO> getListByUsernameOrRealName(String name);
}
