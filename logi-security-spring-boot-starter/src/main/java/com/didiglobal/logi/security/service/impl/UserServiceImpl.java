package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.po.RolePO;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.po.UserRolePO;
import com.didiglobal.logi.security.common.dto.role.AssignDataDTO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.*;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.service.PermissionService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author cjm
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private DeptService deptService;

    @Override
    public PagingData<UserVO> getUserPage(UserQueryDTO queryVo) {
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<UserPO> userPage = new Page<>(queryVo.getPage(), queryVo.getSize());

        // 是否有角色条件
        if (queryVo.getRoleId() != null) {
            RolePO rolePO = roleMapper.selectById(queryVo.getRoleId());
            if (rolePO == null) {
                // 数据库没该角色名字
                return new PagingData<>(userPage);
            }
            // 根据角色id查找用户idList
            QueryWrapper<UserRolePO> wrapper = new QueryWrapper<>();
            wrapper.select("user_id").eq("role_id", rolePO.getId());
            List<Object> userIdList = userRoleMapper.selectObjs(wrapper);
            // 只获取拥有该角色的用户信息
            userWrapper.in("id", userIdList);
        }

        List<Integer> deptIdList = deptService.getChildDeptIdListByParentId(queryVo.getDeptId());
        userWrapper
                .in(queryVo.getDeptId() != null, "dept_id", deptIdList)
                .like(queryVo.getUsername() != null, "username", queryVo.getUsername())
                .like(queryVo.getRealName() != null, "real_name", queryVo.getRealName());

        userMapper.selectPage(userPage, userWrapper);
        // 转成vo
        List<UserVO> userVOList = CopyBeanUtil.copyList(userPage.getRecords(), UserVO.class);

        // 获取所有角色，并转换成 roleId-Role对象 形式
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        roleWrapper.select("id", "role_name");
        Map<Integer, RolePO> roleMap = roleMapper.selectList(roleWrapper)
                .stream().collect(Collectors.toMap(RolePO::getId, rolePO -> rolePO));

        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        for (int i = 0; i < userVOList.size(); i++) {
            UserVO userVo = userVOList.get(i);
            // 查询用户关联的角色
            userRoleWrapper.select("role_id").eq("user_id", userVo.getId());
            List<Object> roleIdList = userRoleMapper.selectObjs(userRoleWrapper);

            List<RoleBriefVO> roleBriefVOList = new ArrayList<>();
            for (Object roleId : roleIdList) {
                RolePO rolePO = roleMap.get((Integer) roleId);
                roleBriefVOList.add(CopyBeanUtil.copy(rolePO, RoleBriefVO.class));
            }
            // 设置角色信息
            userVo.setRoleList(roleBriefVOList);
            userRoleWrapper.clear();

            // 查找用户所在部门信息
            Integer deptId = userPage.getRecords().get(i).getDeptId();
            userVo.setDeptList(deptService.getParentDeptListByChildId(deptId));
            userVo.setUpdateTime(userPage.getRecords().get(i).getUpdateTime().getTime());
            // 隐私信息处理
            privacyProcessing(userVo);
        }
        return new PagingData<>(userVOList, userPage);
    }

    @Override
    public UserVO getDetailById(Integer userId) {
        UserPO userPO = userMapper.selectById(userId);
        if (userPO == null) {
            throw new SecurityException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        // 根据用户id获取角色idList
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.select("role_id").eq("user_id", userId);
        List<Object> roleIdList = userRoleMapper.selectObjs(userRoleWrapper);

        Set<Integer> permissionHasSet = new HashSet<>();
        QueryWrapper<RolePermissionPO> rolePermissionWrapper = new QueryWrapper<>();

        List<RoleBriefVO> roleBriefVOList = new ArrayList<>();
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        for (Object roleId : roleIdList) {
            // 获取角色信息
            roleWrapper.clear();
            roleWrapper.select("id", "role_name").eq("id", roleId);
            RolePO rolePO = roleMapper.selectOne(roleWrapper);
            roleBriefVOList.add(CopyBeanUtil.copy(rolePO, RoleBriefVO.class));

            // 查询该角色拥有的权限idList
            rolePermissionWrapper.select("permission_id").eq("role_id", roleId);
            List<Object> permissionIdList = rolePermissionMapper.selectObjs(rolePermissionWrapper);

            // 添加到用户拥有的所有权限集合
            for (Object permissionId : permissionIdList) {
                permissionHasSet.add((Integer) permissionId);
            }

            rolePermissionWrapper.clear();
        }
        UserVO userVo = CopyBeanUtil.copy(userPO, UserVO.class);
        // 设置角色信息
        userVo.setRoleList(roleBriefVOList);
        // 构建权限树
        userVo.setPermissionTreeVO(permissionService.buildPermissionTree(permissionHasSet));
        // 查找用户所在部门信息
        userVo.setDeptList(deptService.getParentDeptListByChildId(userPO.getDeptId()));
        userVo.setUpdateTime(userPO.getUpdateTime().getTime());
        return userVo;
    }

    @Override
    public List<UserBriefVO> getListByUsernameOrRealName(String name) {
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "real_name")
                .like(!StringUtils.isEmpty(name), "username", name)
                .or()
                .like(!StringUtils.isEmpty(name), "real_name", name);
        List<UserPO> userList = userMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(userList, UserBriefVO.class);
    }

    @Override
    public List<UserBriefVO> getListByDeptId(Integer deptId) {
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
        List<Integer> deptIdList = deptService.getChildDeptIdListByParentId(deptId);
        // 根据部门id查找用户，该部门的子部门的用户都属于该部门
        userWrapper.select("id", "username", "real_name").in("dept_id", deptIdList);
        List<UserPO> userList = userMapper.selectList(userWrapper);
        return CopyBeanUtil.copyList(userList, UserBriefVO.class);
    }

    @Override
    public List<AssignDataDTO> getAssignDataByUserId(Integer userId, String roleName) {
        if(userId == null) {
            throw new SecurityException(ResultCode.USER_ID_CANNOT_BE_NULL);
        }
        // 查询所有的角色，并根据角色添加时间排序（倒序）
        QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "role_name")
                .like(!StringUtils.isEmpty(roleName), "role_name", roleName);
        List<RolePO> roleList = roleMapper.selectList(queryWrapper);

        // 先获取该用户已拥有的角色，并转为set
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.select("role_id").eq("user_id", userId);
        List<Object> roleIdList = userRoleMapper.selectObjs(userRoleWrapper);
        Set<Object> hasRoleIdSet = new HashSet<>(roleIdList);

        // 封装List<AssignDataVo>
        List<AssignDataDTO> list = new ArrayList<>();
        for(RolePO rolePO : roleList) {
            AssignDataDTO data = new AssignDataDTO();
            data.setName(rolePO.getRoleName());
            data.setId(rolePO.getId());
            data.setHas(hasRoleIdSet.contains(rolePO.getId()));
            list.add(data);
        }
        return list;
    }

    @Override
    public List<UserBriefVO> getListByRoleId(Integer roleId) {
        if(roleId == null) {
            throw new SecurityException(ResultCode.ROLE_ID_CANNOT_BE_NULL);
        }
        // 先获取拥有该角色的用户id
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.select("user_id").eq("role_id", roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(userRoleWrapper);

        // 封装List<UserPO>
        QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
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