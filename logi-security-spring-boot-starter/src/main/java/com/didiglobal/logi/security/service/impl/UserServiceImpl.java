package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.entity.user.UserBrief;
import com.didiglobal.logi.security.common.vo.role.AssignInfoVO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.dao.UserDao;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.service.*;
import com.didiglobal.logi.security.util.CopyBeanUtil;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author cjm
 */
@Service
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

    @Override
    public PagingData<UserVO> getUserPage(UserQueryDTO queryDTO) {
        List<Integer> userIdList = null;
        // 是否有角色id条件
        if(queryDTO.getRoleId() != null) {
            // 根据角色获取用户IdList
            userIdList = userRoleService.getUserIdListByRoleId(queryDTO.getRoleId());
        }
        // 获取该部门下的所有子部门idList
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(queryDTO.getDeptId());

        IPage<User> iPage = userDao.selectPageByDeptIdListAndUserIdList(queryDTO, deptIdList, userIdList);
        List<UserVO> userVOList = new ArrayList<>();
        List<User> userList = iPage.getRecords();
        for (User user : userList) {
            UserVO userVo = CopyBeanUtil.copy(user, UserVO.class);
            // 设置角色信息
            userVo.setRoleList(roleService.getRoleBriefListByUserId(userVo.getId()));
            // 设置部门信息
            userVo.setDeptList(deptService.getDeptBriefListByChildId(user.getDeptId()));
            userVo.setUpdateTime(user.getUpdateTime().getTime());
            // 隐私信息处理
            privacyProcessing(userVo);
            userVOList.add(userVo);
        }
        return new PagingData<>(userVOList, iPage);
    }

    @Override
    public PagingData<UserBriefVO> getUserBriefPage(UserBriefQueryDTO queryDTO) {
        // 查找合适的部门idList
        List<Integer> deptIdList = deptService.getDeptIdListByParentIdAndDeptName(queryDTO.getDeptId(), queryDTO.getDeptName());
        // 分页获取
        IPage<UserBrief> iPage = userDao.selectBriefPageByDeptIdList(queryDTO, deptIdList);
        List<UserBriefVO> userBriefVOList = CopyBeanUtil.copyList(iPage.getRecords(), UserBriefVO.class);
        return new PagingData<>(userBriefVOList, iPage);
    }

    @Override
    public UserVO getUserDetailByUserId(Integer userId) {
        User user = userDao.selectByUserId(userId);
        if (user == null) {
            return null;
        }
        // 根据用户id获取角色List
        List<RoleBriefVO> roleBriefVOList = roleService.getRoleBriefListByUserId(userId);

        List<Integer> permissionHasSet = new ArrayList<>();
        for (RoleBriefVO roleBriefVO : roleBriefVOList) {
            // 获取角色拥有的权限idList
            List<Integer> permissionIdList = rolePermissionService.getPermissionIdListByRoleId(roleBriefVO.getId());
            // 添加到用户拥有的所有权限集合
            permissionHasSet.addAll(permissionIdList);
        }

        UserVO userVo = CopyBeanUtil.copy(user, UserVO.class);
        // 设置角色信息
        userVo.setRoleList(roleBriefVOList);
        // 构建权限树
        userVo.setPermissionTreeVO(permissionService.buildPermissionTreeWithHas(permissionHasSet));
        // 查找用户所在部门信息
        userVo.setDeptList(deptService.getDeptBriefListByChildId(user.getDeptId()));
        userVo.setUpdateTime(user.getUpdateTime().getTime());
        return userVo;
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
    public List<Integer> getUserIdListByUsernameOrRealName(String name) {
        List<UserBrief> userBriefList = userDao.selectBriefListByNameAndDescOrderByCreateTime(name);
        List<Integer> result = new ArrayList<>();
        for(UserBrief userBrief : userBriefList) {
            result.add(userBrief.getId());
        }
        return result;
    }

    @Override
    public List<UserBriefVO> getUserBriefListByDeptId(Integer deptId) {
        // 根据部门id查找用户，该部门的子部门的用户都属于该部门
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(deptId);
        List<UserBrief> userBriefList = userDao.selectBriefListByDeptIdList(deptIdList);
        return CopyBeanUtil.copyList(userBriefList, UserBriefVO.class);
    }

    @Override
    public List<AssignInfoVO> getAssignDataByUserId(Integer userId, String roleName) throws LogiSecurityException {
        if(userId == null) {
            throw new LogiSecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        // 根据角色名，模糊查询
        List<RoleBriefVO> roleBriefVOList = roleService.getRoleBriefListByRoleName(roleName);
        // 获取该用户拥有的角色
        List<RoleBriefVO> hasRoleBriefVOList = roleService.getRoleBriefListByUserId(userId);
        // 把角色id转为set
        Set<Integer> hasRoleIdSet = new HashSet<>();
        for(RoleBriefVO roleBriefVO : hasRoleBriefVOList) {
            hasRoleIdSet.add(roleBriefVO.getId());
        }
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