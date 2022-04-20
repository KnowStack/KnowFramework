package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.dto.user.UserDTO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.entity.user.UserBrief;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.vo.role.AssignInfoVO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.dao.UserDao;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.service.*;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.didiglobal.logi.security.common.enums.ResultCode.*;

/**
 * @author cjm
 */
@Service("logiSecurityUserServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private OplogService oplogService;

    @Override
    public PagingData<UserVO> getUserPage(UserQueryDTO queryDTO) {
        List<Integer> userIdList = null;
        // 是否有角色id条件
        if(queryDTO.getRoleId() != null) {
            // 根据角色获取用户IdList
            userIdList = userRoleService.getUserIdListByRoleId(queryDTO.getRoleId());
            if(CollectionUtils.isEmpty(userIdList)){
                return new PagingData<>(new Page<>(queryDTO.getPage(), queryDTO.getSize()));
            }
        }

        IPage<User> pageInfo = userDao.selectPageByUserIdList(queryDTO, userIdList);
        List<UserVO> userVOList = new ArrayList<>();
        List<User> userList = pageInfo.getRecords();

        // 提前获取所有部门
        for (User user : userList) {
            UserVO userVo = CopyBeanUtil.copy(user, UserVO.class);
            // 设置角色信息
            userVo.setRoleList(roleService.getRoleBriefListByUserId(userVo.getId()));
            userVo.setUpdateTime(user.getUpdateTime());
            userVo.setCreateTime(user.getCreateTime());
            // 隐私信息处理
            privacyProcessing(userVo);
            userVOList.add(userVo);
        }
        return new PagingData<>(userVOList, pageInfo);
    }

    @Override
    public PagingData<UserBriefVO> getUserBriefPage(UserBriefQueryDTO queryDTO) {
        // 查找合适的部门idList
        List<Integer> deptIdList = deptService.
                getDeptIdListByParentIdAndDeptName(queryDTO.getDeptId(), queryDTO.getDeptName());
        // 分页获取
        IPage<UserBrief> pageInfo = userDao.selectBriefPageByDeptIdList(queryDTO, deptIdList);
        List<UserBriefVO> userBriefVOList = CopyBeanUtil.copyList(pageInfo.getRecords(), UserBriefVO.class);
        return new PagingData<>(userBriefVOList, pageInfo);
    }

    @Override
    public UserVO getUserDetailByUserId(Integer userId) throws LogiSecurityException {
        User user = userDao.selectByUserId(userId);
        if (user == null) {
            throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
        }
        UserVO userVo = CopyBeanUtil.copy(user, UserVO.class);

        // 根据用户id获取角色List
        List<RoleBriefVO> roleBriefVOList = roleService.getRoleBriefListByUserId(userId);
        // 设置角色信息
        userVo.setRoleList(roleBriefVOList);

        List<Integer> roleIdList = roleBriefVOList.stream().map(RoleBriefVO::getId).collect(Collectors.toList());
        // 根据角色idList获取权限idList
        List<Integer> hasPermissionIdList = rolePermissionService.getPermissionIdListByRoleIdList(roleIdList);
        // 构建权限树
        userVo.setPermissionTreeVO(permissionService.buildPermissionTreeWithHas(hasPermissionIdList));

        userVo.setUpdateTime(user.getUpdateTime());
        userVo.setCreateTime(user.getCreateTime());
        return userVo;
    }

    @Override
    public Result<Void> deleteByUserId(Integer userId) {
        if(userId == null) {
            return Result.fail("userId is null!");
        }

        boolean success = userDao.deleteByUserId(userId);
        if(success){
            userRoleService.deleteByUserIdOrRoleId(userId, null);
        }

        return success ? Result.success() : Result.fail();
    }

    @Override
    public UserBriefVO getUserBriefByUserId(Integer userId) {
        if(userId == null) {
            return null;
        }
        User user = userDao.selectByUserId(userId);
        return CopyBeanUtil.copy(user, UserBriefVO.class);
    }

    @Override
    public User getUserByUserName(String userName){
        return userDao.selectByUsername(userName);
    }

    @Override
    public List<UserBriefVO> getUserBriefListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        List<UserBrief> userBriefList = userDao.selectBriefListByUserIdList(userIdList);
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public List<UserBriefVO> getUserBriefListByUsernameOrRealName(String name) {
        List<UserBrief> userBriefList = userDao.selectBriefListByNameAndDescOrderByCreateTime(name);
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public List<UserBriefVO> getAllUserBriefListOrderByCreateTime(boolean isAsc) {
        List<UserBrief> userBriefList = userDao.selectBriefListOrderByCreateTime(isAsc);
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public List<Integer> getUserIdListByUsernameOrRealName(String name) {
        return userDao.selectUserIdListByUsernameOrRealName(name);
    }

    @Override
    public List<UserBriefVO> getAllUserBriefList() {
        List<UserBrief> userBriefList = userDao.selectAllBriefList();
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public List<UserBriefVO> getUserBriefListByDeptId(Integer deptId) {
        // 根据部门id查找用户，该部门的子部门的用户都属于该部门
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(deptId);
        List<UserBrief> userBriefList = userDao.selectBriefListByDeptIdList(deptIdList);
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public List<AssignInfoVO> getAssignDataByUserId(Integer userId) throws LogiSecurityException {
        if(userId == null) {
            throw new LogiSecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        // 获取所有角色
        List<RoleBriefVO> roleBriefVOList = roleService.getAllRoleBriefList();
        // 获取该用户拥有的角色
        Set<Integer> hasRoleIdSet = new HashSet<>(userRoleService.getRoleIdListByUserId(userId));

        List<AssignInfoVO> list = new ArrayList<>();
        for(RoleBriefVO roleBriefVO : roleBriefVOList) {
            AssignInfoVO data = new AssignInfoVO();
            data.setName(roleBriefVO.getRoleName());
            data.setId(roleBriefVO.getId());
            data.setHas(hasRoleIdSet.contains(roleBriefVO.getId()));
            list.add(data);
        }
        return list;
    }

    @Override
    public List<UserBriefVO> getUserBriefListByRoleId(Integer roleId) {
        // 先获取拥有该角色的用户id
        List<Integer> userIdList = userRoleService.getUserIdListByRoleId(roleId);
        List<UserBrief> userBriefList = userDao.selectBriefListByUserIdList(userIdList);
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public Result<Void> addUser(UserDTO userDTO, String operator) {
        if(null != userDao.selectByUsername(userDTO.getUserName())){
            return Result.fail(USER_ACCOUNT_ALREADY_EXIST);
        }

        try {
            UserPO userPO = CopyBeanUtil.copy(userDTO, UserPO.class);
            if(userDao.addUser(userPO) > 0){
                userRoleService.updateUserRoleByUserId(userPO.getId(), userDTO.getRoleIds());
            }
        } catch (Exception e) {
            return Result.fail(USER_ACCOUNT_INSERT_FAIL);
        }

        return Result.success();
    }

    @Override
    public Result<Void> editUser(UserDTO userDTO, String operator) {
        User user = userDao.selectByUsername(userDTO.getUserName());
        if(null == user){
            return Result.fail(USER_ACCOUNT_NOT_EXIST);
        }

        try {
            UserPO userPO = CopyBeanUtil.copy(userDTO, UserPO.class);
            userPO.setId(user.getId());
            if(userDao.editUser(userPO) > 0){
                userRoleService.updateUserRoleByUserId(userPO.getId(), userDTO.getRoleIds());
            }
        } catch (Exception e) {
            return Result.fail(USER_ACCOUNT_UPDATE_FAIL);
        }

        return Result.success();
    }

    /**
     * 隐私处理
     *
     * @param userVo 返回给页面的用户信息
     */
    private void privacyProcessing(UserVO userVo) {
        String phone = userVo.getPhone();
        userVo.setPhone(phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
    }
}