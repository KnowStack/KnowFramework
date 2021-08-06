package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.MessageDto;
import com.didiglobal.logi.security.common.entity.*;
import com.didiglobal.logi.security.common.enums.message.MessageCode;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.common.vo.role.RoleAssignVo;
import com.didiglobal.logi.security.common.vo.role.RoleQueryVo;
import com.didiglobal.logi.security.common.vo.role.RoleSaveVo;
import com.didiglobal.logi.security.common.vo.role.RoleVo;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.extend.MessageExtend;
import com.didiglobal.logi.security.mapper.*;
import com.didiglobal.logi.security.service.PermissionService;
import com.didiglobal.logi.security.service.RoleService;

import java.text.SimpleDateFormat;
import java.util.*;

import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MessageExtend messageExtend;

    @Override
    public RoleVo getDetailById(Integer id) {
        if(id == null) {
            throw new SecurityException(ResultCode.PARAM_NOT_VALID);
        }
        Role role = roleMapper.selectById(id);
        if(role == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        // 根据角色id去查权限树
        PermissionVo permissionVo = buildPermissionTree(role.getId());
        RoleVo roleVo = CopyBeanUtil.copy(role, RoleVo.class);
        roleVo.setPermissionVo(permissionVo);
        roleVo.setCreateTime(role.getCreateTime().getTime());
        return roleVo;
    }

    @Override
    public PagingData<RoleVo> getRolePage(RoleQueryVo queryVo) {
        // 分页查询
        IPage<Role> rolePage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper
                .like(queryVo.getRoleCode() != null, "role_code", queryVo.getRoleCode())
                .like(queryVo.getRoleName() != null, "role_name", queryVo.getRoleName())
                .like(queryVo.getDescription() != null, "description", queryVo.getDescription());
        roleMapper.selectPage(rolePage, roleWrapper);
        // 转vo
        List<RoleVo> roleVoList = CopyBeanUtil.copyList(rolePage.getRecords(), RoleVo.class);
        // 统计角色关联用户数
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        for(int i = 0; i < roleVoList.size(); i++) {
            RoleVo roleVo = roleVoList.get(i);
            // 获取该角色已分配给的用户数
            userRoleWrapper.eq("role_id", roleVo.getId());
            roleVo.setAuthedUserCnt(userRoleMapper.selectCount(userRoleWrapper));
            roleVo.setCreateTime(rolePage.getRecords().get(i).getCreateTime().getTime());
            userRoleWrapper.clear();
        }
        return new PagingData<>(roleVoList, rolePage);
    }

    @Override
    public void createRole(RoleSaveVo roleSaveVo) {
        // 检查参数
        checkParam(roleSaveVo);
        // 保存角色信息
        Role role = CopyBeanUtil.copy(roleSaveVo, Role.class);
        // TODO；这里要添加修改人信息（Token中获取）
        // role.setLastReviser("xxx");
        // 设置角色编号
        role.setRoleCode("role" + ((int)((Math.random() + 1) * 10000)));
        roleMapper.insert(role);
        List<Integer> permissionIdList = roleSaveVo.getPermissionIdList();
        if(!CollectionUtils.isEmpty(permissionIdList)) {
            List<RolePermission> rolePermissionList = getRolePermissionList(role.getId(), permissionIdList);
            // 保存角色与权限信息（批量插入）
            rolePermissionMapper.insertBatchSomeColumn(rolePermissionList);
        }
    }

    @Override
    public void deleteRoleById(Integer id) {
        Role role = roleMapper.selectById(id);
        if(role == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        // 检查该角色是否和用户绑定
        QueryWrapper<UserRole> userRoleMapperWrapper = new QueryWrapper<>();
        userRoleMapperWrapper.eq("role_id", id);
        if(userRoleMapper.selectCount(userRoleMapperWrapper) > 0) {
            throw new SecurityException(ResultCode.ROLE_USER_AUTHED);
        }
        // 删除角色与权限的关联
        QueryWrapper<RolePermission> rolePermissionWrapper = new QueryWrapper<>();
        rolePermissionWrapper.eq("role_id", id);
        rolePermissionMapper.delete(rolePermissionWrapper);
        // 逻辑删除（自动）
        roleMapper.deleteById(role.getId());
    }

    @Override
    public void updateRoleById(RoleSaveVo roleSaveVo) {
        if(roleSaveVo == null || roleMapper.selectById(roleSaveVo.getId()) == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        checkParam(roleSaveVo);
        // 更新角色基本信息
        Role role = CopyBeanUtil.copy(roleSaveVo, Role.class);
        roleMapper.updateById(role);
        // 先删除旧的角色与权限关联信息
        QueryWrapper<RolePermission> rolePermissionWrapper = new QueryWrapper<>();
        rolePermissionWrapper.eq("role_id", role.getId());
        rolePermissionMapper.delete(rolePermissionWrapper);
        // 更新角色与权限关联信息
        if(!CollectionUtils.isEmpty(roleSaveVo.getPermissionIdList())) {
            List<RolePermission> rpList = getRolePermissionList(role.getId(), roleSaveVo.getPermissionIdList());
            rolePermissionMapper.insertBatchSomeColumn(rpList);
        }
    }

    @Override
    public void assignRoles(RoleAssignVo roleAssignVo) {
        // 获取old用户与角色的关系
        List<Object> oldIdList = getOldRelation(roleAssignVo);

        if(roleAssignVo.getFlag() == null) {
            throw new SecurityException(ResultCode.PARAM_NOT_VALID);
        }
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        Integer id = roleAssignVo.getId();
        List<UserRole> userRoleList = new ArrayList<>();
        if(roleAssignVo.getFlag()) {
            // N个角色分配给1个用户
            if(id == null || userMapper.selectById(id) == null) {
                throw new SecurityException(ResultCode.USER_ACCOUNT_NOT_EXIST);
            }
            userRoleWrapper.eq("user_id", id);
            // 添加new角色用户关联信息
            for(Integer roleId : roleAssignVo.getIdList()) {
                userRoleList.add(new UserRole(id, roleId));
            }
        } else {
            // 1个角色分配给N个用户
            if(id == null || roleMapper.selectById(id) == null) {
                throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
            }
            userRoleWrapper.eq("role_id", id);
            for(Integer userIs : roleAssignVo.getIdList()) {
                userRoleList.add(new UserRole(userIs, id));
            }
        }
        // 删除old的全部角色用户关联信息
        userRoleMapper.delete(userRoleWrapper);
        // 插入new的角色与用户关联关系
        userRoleMapper.insertBatchSomeColumn(userRoleList);

        // 打包和保存角色更新消息 TODO 后面改为异步
        packAndSaveMessage(oldIdList, roleAssignVo);
    }

    /**
     * 记录old的角色与用户的关系
     * @param roleAssignVo 条件
     * @return old用户拥有的角色idList，或old角色已分配的用户idList
     */
    private List<Object> getOldRelation(RoleAssignVo roleAssignVo) {
        // 获取传入方法的参数
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        if (roleAssignVo.getFlag()) {
            // 如果是N个角色分配给1个用户
            userRoleWrapper.select("role_id").eq("user_id", roleAssignVo.getId());
        } else {
            // 1个角色分配给N个用户
            userRoleWrapper.select("user_id").eq("role_id", roleAssignVo.getId());
        }
        // 获取old用户拥有的角色idList，或获取old角色已分配的用户idList
        return userRoleMapper.selectObjs(userRoleWrapper);
    }

    private void packAndSaveMessage(List<Object> oldIdList, RoleAssignVo roleAssignVo) {
        List<Integer> newIdList = roleAssignVo.getIdList();

        List<Integer> removeIdList = new ArrayList<>();
        List<Integer> addIdList = new ArrayList<>();
        // 取交集
        Set<Integer> set = getIntersection(oldIdList, newIdList);
        for(Object oldId : oldIdList) {
            if(!set.contains((Integer) oldId)) {
                removeIdList.add((Integer) oldId);
            }
        }
        for(Integer newId : newIdList) {
            if(!set.contains(newId)) {
                addIdList.add(newId);
            }
        }

        if (roleAssignVo.getFlag()) {
            // 如果是N个角色分配给1个用户，oldIdList和newIdList都为角色idList
            Integer userId= roleAssignVo.getId();
            // 保存移除角色消息
            saveRoleAssignMessage(userId, removeIdList, MessageCode.ROLE_REMOVE_MESSAGE);
            // 保存新增角色消息
            saveRoleAssignMessage(userId, addIdList, MessageCode.ROLE_ADD_MESSAGE);
        } else {
            // 1个角色分配给N个用户，oldIdList和newIdList都为用户idList
            List<Integer> roleIdList = new ArrayList<>();
            roleIdList.add(roleAssignVo.getId());
            for(Integer userId : removeIdList) {
                // 保存移除角色消息
                saveRoleAssignMessage(userId, roleIdList, MessageCode.ROLE_REMOVE_MESSAGE);
            }
            for(Integer userId : addIdList) {
                // 保存新增角色消息
                saveRoleAssignMessage(userId, roleIdList, MessageCode.ROLE_ADD_MESSAGE);
            }
        }
    }

    /**
     * 保存用户角色变更消息
     * @param userId 角色变更的用户id
     * @param roleIdList 变更的角色
     * @param messageCode 变更消息，是移除角色还是添加角色
     */
    private void saveRoleAssignMessage(Integer userId, List<Integer> roleIdList, MessageCode messageCode) {
        if(CollectionUtils.isEmpty(roleIdList)) {
            return;
        }
        MessageDto messageDto = new MessageDto();
        // 设置消息所属用户
        messageDto.setUserId(userId);
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper.select("role_name").in("id", roleIdList);
        List<Object> roleNameList = roleMapper.selectObjs(roleWrapper);
        // 拼接角色信息
        String info = spliceRoleName(roleNameList);
        // 获取当前时间
        SimpleDateFormat formatter= new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);
        // 赋值占位符
        String content = String.format(messageCode.getContent(), time, info);
        messageDto.setTitle(messageCode.getTitle());
        messageDto.setContent(content);
        messageExtend.saveMessage(messageDto);
    }

    /**
     * 根据角色id，查找所有权限
     * @param roleId 角色id
     * @return 权限树
     */
    private PermissionVo buildPermissionTree(Integer roleId) {
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        // 获取该角色拥有的全部权限id
        wrapper.select("permission_id").eq("role_id", roleId);
        List<Object> permissionIdList = rolePermissionMapper.selectObjs(wrapper);

        Set<Integer> permissionHasSet = new HashSet<>();
        for(Object permissionId : permissionIdList) {
            permissionHasSet.add((Integer) permissionId);
        }

        return permissionService.buildPermissionTree(permissionHasSet);
    }

    /**
     * 添加或者修改时候检查参数
     * @param roleSaveVo 角色信息
     */
    private void checkParam(RoleSaveVo roleSaveVo) {
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper
                .eq("role_name", roleSaveVo.getRoleName())
                // 如果有id信息，说明是更新，则判断角色名重复的时候要排除old信息
                .ne(roleSaveVo.getId() != null, "id", roleSaveVo.getId());
        if(roleMapper.selectOne(roleWrapper) != null) {
            throw new SecurityException(ResultCode.ROLE_ALREADY_EXIST);
        }
    }

    /**
     * 用于构建可以直接插入角色与权限中间表的数据
     * @param roleId 角色Id
     * @param permissionIdList 权限idList
     * @return List<RolePermission>
     */
    private List<RolePermission> getRolePermissionList(Integer roleId, List<Integer> permissionIdList) {
        List<RolePermission> rolePermissionList = new ArrayList<>();
        for(Integer permissionId : permissionIdList) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionList.add(rolePermission);
        }
        return rolePermissionList;
    }

    /**
     * 求两个数组的交集
     *
     * @param list1 数组1
     * @param list2 数组2
     * @return 交集元素
     */
    private Set<Integer> getIntersection(List<Object> list1, List<Integer> list2) {
        Set<Integer> result = new HashSet<>();

        // 将较长的数组转换为set
        Set<Integer> set = new HashSet<>(list2);

        // 遍历较短的数组
        for (Object obj : list1) {
            Integer num = (Integer) obj;
            if (set.contains(num)) {
                result.add(num);
            }
        }
        return result;
    }

    /**
     * 拼接角色名
     * @param roleNameList 角色名List
     * @return 拼接后
     */
    private String spliceRoleName(List<Object> roleNameList) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < roleNameList.size() - 1; i++) {
            sb.append(roleNameList.get(i)).append(",");
        }
        sb.append(roleNameList.get(roleNameList.size() - 1));
        return sb.toString();
    }

}