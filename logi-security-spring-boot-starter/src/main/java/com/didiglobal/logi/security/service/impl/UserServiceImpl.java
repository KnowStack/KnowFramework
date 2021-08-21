package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.user.UserBriefQueryDTO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.vo.role.AssignInfoVO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.*;
import com.didiglobal.logi.security.service.*;
import com.didiglobal.logi.security.util.CopyBeanUtil;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author cjm
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

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
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<UserPO> userPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        // 是否有角色id条件
        if(queryDTO.getRoleId() != null) {
            // 根据角色获取用户IdList
            List<Integer> userIdList = userRoleService.getUserIdListByRoleId(queryDTO.getRoleId());
            if(CollectionUtils.isEmpty(userIdList)) {
                return new PagingData<>(userPage);
            }
            // 只获取拥有该角色的用户信息
            userWrapper.in("id", userIdList);
        }
        // 获取该部门下的所有子部门idList
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(queryDTO.getDeptId());
        userWrapper
                .in(queryDTO.getDeptId() != null, "dept_id", deptIdList)
                .like(queryDTO.getUsername() != null, "username", queryDTO.getUsername())
                .like(queryDTO.getRealName() != null, "real_name", queryDTO.getRealName());
        userMapper.selectPage(userPage, userWrapper);

        List<UserVO> userVOList = new ArrayList<>();
        List<UserPO> userPOList = userPage.getRecords();
        for (UserPO userPO : userPOList) {
            UserVO userVo = CopyBeanUtil.copy(userPO, UserVO.class);
            // 设置角色信息
            userVo.setRoleList(roleService.getRoleBriefListByUserId(userVo.getId()));
            // 设置部门信息
            userVo.setDeptList(deptService.getDeptBriefListByChildId(userPO.getDeptId()));
            userVo.setUpdateTime(userPO.getUpdateTime().getTime());
            // 隐私信息处理
            privacyProcessing(userVo);
            userVOList.add(userVo);
        }
        return new PagingData<>(userVOList, userPage);
    }

    @Override
    public PagingData<UserBriefVO> getUserBriefPage(UserBriefQueryDTO queryDTO) {
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        IPage<UserPO> userPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 查找合适的部门idList
        List<Integer> deptIdList = deptService.getDeptIdListByParentIdAndDeptName(queryDTO.getDeptId(), queryDTO.getDeptName());

        userWrapper
                .like(queryDTO.getUsername() != null, "username", queryDTO.getUsername())
                .like(queryDTO.getRealName() != null, "real_name", queryDTO.getRealName())
                .in(!CollectionUtils.isEmpty(deptIdList), "dept_id", deptIdList);
        userMapper.selectPage(userPage, userWrapper);

        List<UserBriefVO> list = CopyBeanUtil.copyList(userPage.getRecords(), UserBriefVO.class);
        return new PagingData<>(list, userPage);
    }

    @Override
    public UserVO getUserDetailByUserId(Integer userId) {
        UserPO userPO = userMapper.selectById(userId);
        if (userPO == null) {
            return null;
        }
        // 根据用户id获取角色List
        List<RoleBriefVO> roleBriefVOList = roleService.getRoleBriefListByUserId(userId);

        Set<Integer> permissionHasSet = new HashSet<>();
        for (RoleBriefVO roleBriefVO : roleBriefVOList) {
            // 获取角色拥有的权限idList
            List<Integer> permissionIdList = rolePermissionService.getPermissionIdListByRoleId(roleBriefVO.getId());
            // 添加到用户拥有的所有权限集合
            permissionHasSet.addAll(permissionIdList);
        }

        UserVO userVo = CopyBeanUtil.copy(userPO, UserVO.class);
        // 设置角色信息
        userVo.setRoleList(roleBriefVOList);
        // 构建权限树
        userVo.setPermissionTreeVO(permissionService.buildPermissionTree(permissionHasSet));
        // 查找用户所在部门信息
        userVo.setDeptList(deptService.getDeptBriefListByChildId(userPO.getDeptId()));
        userVo.setUpdateTime(userPO.getUpdateTime().getTime());
        return userVo;
    }

    @Override
    public UserBriefVO getUserBriefByUserId(Integer userId) {
        if(userId == null) {
            return null;
        }
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "real_name").eq("id", userId);
        UserPO userPO = userMapper.selectOne(queryWrapper);
        return CopyBeanUtil.copy(userPO, UserBriefVO.class);
    }

    @Override
    public List<UserBriefVO> getUserBriefListByUserIdList(List<Integer> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "real_name").in("id", userIdList);
        List<UserPO> userPOList = userMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(userPOList, UserBriefVO.class);
    }

    @Override
    public List<UserBriefVO> getUserBriefListByUsernameOrRealName(String name) {
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "real_name")
                .like(!StringUtils.isEmpty(name), "username", name)
                .or()
                .like(!StringUtils.isEmpty(name), "real_name", name)
                .orderByDesc("create_time");
        List<UserPO> userList = userMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(userList, UserBriefVO.class);
    }

    @Override
    public List<Integer> getUserIdListByUsernameOrRealName(String name) {
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id")
                .like(!StringUtils.isEmpty(name), "username", name)
                .or()
                .like(!StringUtils.isEmpty(name), "real_name", name)
                .orderByDesc("create_time");
        List<Object> userIdList = userMapper.selectObjs(queryWrapper);
        List<Integer> result = new ArrayList<>();
        for(Object userId : userIdList) {
            result.add((Integer) userId);
        }
        return result;
    }

    @Override
    public List<UserBriefVO> getUserBriefListByDeptId(Integer deptId) {
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        List<Integer> deptIdList = deptService.getDeptIdListByParentId(deptId);
        // 根据部门id查找用户，该部门的子部门的用户都属于该部门
        userWrapper
                .select("id", "username", "real_name")
                .in(!CollectionUtils.isEmpty(deptIdList), "dept_id", deptIdList);
        List<UserPO> userList = userMapper.selectList(userWrapper);
        return CopyBeanUtil.copyList(userList, UserBriefVO.class);
    }

    @Override
    public List<AssignInfoVO> getAssignDataByUserId(Integer userId, String roleName) {
        if(userId == null) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
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

        // 封装List<UserPO>
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        if(CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        userWrapper.select("id", "username", "real_name").in("id", userIdList);
        List<UserPO> userList = userMapper.selectList(userWrapper);
        return CopyBeanUtil.copyList(userList, UserBriefVO.class);
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