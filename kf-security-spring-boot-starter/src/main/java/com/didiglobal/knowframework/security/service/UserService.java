package com.didiglobal.knowframework.security.service;

import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.vo.user.UserBasicVO;
import com.didiglobal.knowframework.security.common.vo.user.UserBriefVO;
import com.didiglobal.knowframework.security.exception.KfSecurityException;
import com.didiglobal.knowframework.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.knowframework.security.common.dto.user.UserDTO;
import com.didiglobal.knowframework.security.common.dto.user.UserQueryDTO;
import com.didiglobal.knowframework.security.common.entity.user.User;
import com.didiglobal.knowframework.security.common.vo.role.AssignInfoVO;
import com.didiglobal.knowframework.security.common.vo.user.UserVO;

import java.util.List;

public interface UserService {

    /**
     * 用户注册信息校验
     * @param type
     * @param value
     * @return
     */
    Result<Void> check(Integer type, String value);

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
     * @throws KfSecurityException 用户不存在
     */
    UserVO getUserDetailByUserId(Integer userId);

    /**
     * 根据用户id删除用户
     * @param userId
     * @return
     */
    Result<Void> deleteByUserId(Integer userId);

    /**
     * 获取用户简要信息
     * @param userName
     * @return 用户简要信息
     */
    UserBriefVO getUserBriefByUserName(String userName);

    /**
     * 获取用户简要信息
     * @param userName 用户名称
     * @return 用户简要信息
     */
    User getUserByUserName(String userName);

    /**
     * 获取用户简要信息List
     * @param userIdList 用户idList
     * @return 用户简要信息List
     */
    List<UserBriefVO> getUserBriefListByUserIdList(List<Integer> userIdList);

    /**
     * 根据部门id获取用户list（获取该部门下所有的用户，包括各种子部门）
     * @param deptId 部门id，如果为null，表示无部门用户
     * @return 用户简要信息list
     */
    List<UserBriefVO> getUserBriefListByDeptId(Integer deptId);

    /**
     * 根据用户id和roleName获取角色list
     * @param userId 用户id
     * @return 分配角色或者分配用户/列表信息
     * @throws KfSecurityException 用户id不可为null
     */
    List<AssignInfoVO> getAssignDataByUserId(Integer userId) throws KfSecurityException;

    /**
     * 根据角色id获取用户list
     * @param roleId 角色Id
     * @return 用户简要信息list
     */
    List<UserBriefVO> getUserBriefListByRoleId(Integer roleId);

    /**
     * 会分别以账户名和实名去模糊查询，返回两者的并集
     * 创建项目，添加项目负责人的时候用到
     * @param name 账户名或实名
     * @return 用户简要信息list
     */
    List<UserBriefVO> getUserBriefListByUsernameOrRealName(String name);

    /**
     * 获取用户简要信息List并根据创建时间排序
     * @param isAsc 是否升序
     * @return 用户简要信息List
     */
    List<UserBriefVO> getAllUserBriefListOrderByCreateTime(boolean isAsc);

    /**
     * 会分别以账户名和实名去模糊查询，返回两者的并集
     * @param name 账户名或实名
     * @return 用户IdList
     */
    List<Integer> getUserIdListByUsernameOrRealName(String name);

    /**
     * 获取所有用户简要信息
     * @return 用户简要信息List
     */
    List<UserBriefVO> getAllUserBriefList();

    /**
     * 增加一个用户
     * @param userDTO
     * @param operator
     * @return
     */
    Result<Void> addUser(UserDTO userDTO, String operator);

    /**
     * 编辑一个用户
     * @param userDTO
     * @param operator
     * @return
     */
    Result<Void> editUser(UserDTO userDTO, String operator);
    
    Result<List<UserVO>> getUserDetailByUserIds(List<Integer> ids);
    
    /**
     * 通过用户ID列表获取用户基本信息列表。
     *
     * @param userIds 要查询的 userId 列表。
     * @return UserBasicVO列表
     */
    List<UserBasicVO> getUserBasicListByUserIdList(List<Integer> userIds);
}