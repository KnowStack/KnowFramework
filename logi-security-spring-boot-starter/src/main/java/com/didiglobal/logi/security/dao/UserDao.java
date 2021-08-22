package com.didiglobal.logi.security.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.po.UserPO;

import java.util.List;

/**
 * @author cjm
 */
public interface UserDao {

    /**
     * 根据指定条件分页查询
     * @param queryDTO 查询条件
     * @param deptIdList 部门idList
     * @param userIdList 用户idList
     * @return 用户信息page
     */
    IPage<UserPO> selectPageByDeptIdListAndUserIdList(UserQueryDTO queryDTO, List<Integer> deptIdList, List<Integer> userIdList);

    /**
     * 根据指定条件分页查询用户简要信息
     * @param queryDTO 查询条件
     * @param deptIdList 部门idList
     * @return 用户简要信息page
     */
    IPage<UserPO> selectBriefPageByDeptIdList(UserBriefQueryDTO queryDTO, List<Integer> deptIdList);

    /**
     * 根据用户id获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    UserPO selectByUserId(Integer userId);

    /**
     * 根据用户idList获取用户简要信息List
     * @param userIdList 用户idList
     * @return 用户简要信息List
     */
    List<UserPO> selectBriefListByUserIdList(List<Integer> userIdList);

    /**
     * 会分别以账户名和实名去模糊查询，返回两者的并集
     * @param name 账户名或实名
     * @return List<UserPO> 用户简要信息list
     */
    List<UserPO> selectBriefListByNameAndDescOrderByCreateTime(String name);

    List<UserPO> selectBriefListByDeptIdList(List<Integer> deptIdList);
}
