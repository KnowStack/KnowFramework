package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.vo.role.AssignInfoVO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;

import java.util.List;

public interface UserService {

    /**
     * 分页获取用户信息
     * @param queryDTO 条件信息
     * @return 用户信息list
     */
    PagingData<UserVO> getUserPage(UserQueryDTO queryDTO);

    /**
     * 分页获取用户简要信息
     * @param queryDTO 条件信息
     * @return 用户简要信息list
     */
    PagingData<UserBriefVO> getUserBriefPage(UserBriefQueryDTO queryDTO);

    /**
     * 获取用户详情（主要是获取用户所拥有的权限信息）
     * @param userId 用户id
     * @return 用户详情
     */
    UserVO getUserDetailByUserId(Integer userId);

    /**
     * 获取用户简要信息
     * @param userId 用户id
     * @return 用户简要信息
     */
    UserBriefVO getUserBriefByUserId(Integer userId);

    /**
     * 获取用户简要信息List
     * @param userIdList 用户idList
     * @return 用户简要信息List
     */
    List<UserBriefVO> getUserBriefListByUserIdList(List<Integer> userIdList);

    /**
     * 根据部门id获取用户list（获取该部门下所有的用户，包括各种子部门）
     * @param deptId 部门id，如果为null，表示无部门用户
     * @return List<UserBriefVO> 用户简要信息list
     */
    List<UserBriefVO> getUserBriefListByDeptId(Integer deptId);

    /**
     * 根据用户id和roleName获取角色list
     * @param userId 用户id
     * @param roleName 角色名
     * @return List<AssignDataVo>
     * @throws LogiSecurityException 用户id不可为null
     */
    List<AssignInfoVO> getAssignDataByUserId(Integer userId, String roleName) throws LogiSecurityException;

    /**
     * 根据角色id获取用户list
     * @param roleId 角色Id
     * @return List<UserBriefVO> 用户简要信息list
     */
    List<UserBriefVO> getUserBriefListByRoleId(Integer roleId);

    /**
     * 会分别以账户名和实名去模糊查询，返回两者的并集
     * 创建项目，添加项目负责人的时候用到
     * @param name 账户名或实名
     * @return List<UserBriefVO> 用户简要信息list
     */
    List<UserBriefVO> getUserBriefListByUsernameOrRealName(String name);

    /**
     * 会分别以账户名和实名去模糊查询，返回两者的并集
     * @param name 账户名或实名
     * @return List<Integer> 用户IdList
     */
    List<Integer> getUserIdListByUsernameOrRealName(String name);

}
