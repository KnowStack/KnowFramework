package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.role.AssignDataDTO;
import com.didiglobal.logi.security.common.dto.role.RoleAssignDTO;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.dto.role.RoleSaveDTO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.role.RoleDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import com.didiglobal.logi.security.common.dto2.MessageDto;
import com.didiglobal.logi.security.common.dto2.OplogDto;
import com.didiglobal.logi.security.common.enums.message.MessageCode;
import com.didiglobal.logi.security.common.po.RolePO;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import com.didiglobal.logi.security.common.po.UserPO;
import com.didiglobal.logi.security.common.po.UserRolePO;
import com.didiglobal.logi.security.common.vo.permission.PermissionTreeVO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.*;
import com.didiglobal.logi.security.service.MessageService;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.service.PermissionService;
import com.didiglobal.logi.security.service.RoleService;

import java.text.SimpleDateFormat;
import java.util.*;

import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
import com.didiglobal.logi.security.util.ThreadPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private MessageService messageService;

    @Autowired
    private OplogService oplogService;

    @Override
    public RoleVO getDetailById(Integer id) {
        if(id == null) {
            throw new SecurityException(ResultCode.PARAM_ID_IS_BLANK);
        }
        RolePO rolePO = roleMapper.selectById(id);
        if(rolePO == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        // 根据角色id去查权限树
        PermissionTreeVO permissionTreeVO = permissionService.buildPermissionTreeByRoleId(rolePO.getId());
        RoleVO roleVo = CopyBeanUtil.copy(rolePO, RoleVO.class);
        roleVo.setPermissionTreeVO(permissionTreeVO);
        roleVo.setCreateTime(rolePO.getCreateTime().getTime());
        // 设置授权用户数
        QueryWrapper<UserRolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", id);
        roleVo.setAuthedUserCnt(userRoleMapper.selectCount(queryWrapper));
        return roleVo;
    }

    @Override
    public PagingData<RoleVO> getRolePage(RoleQueryDTO queryVo) {
        // 分页查询
        IPage<RolePO> rolePage = new Page<>(queryVo.getPage(), queryVo.getSize());
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(queryVo.getRoleCode())) {
            roleWrapper.eq("role_code", queryVo.getRoleCode());
        } else {
            roleWrapper
                    .like(queryVo.getRoleName() != null, "role_name", queryVo.getRoleName())
                    .like(queryVo.getDescription() != null, "description", queryVo.getDescription());
        }
        roleMapper.selectPage(rolePage, roleWrapper);
        // 转vo
        List<RoleVO> roleVOList = CopyBeanUtil.copyList(rolePage.getRecords(), RoleVO.class);
        // 统计角色关联用户数
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        for(int i = 0; i < roleVOList.size(); i++) {
            RoleVO roleVo = roleVOList.get(i);
            // 获取该角色已分配给的用户数
            userRoleWrapper.eq("role_id", roleVo.getId());
            roleVo.setAuthedUserCnt(userRoleMapper.selectCount(userRoleWrapper));
            roleVo.setCreateTime(rolePage.getRecords().get(i).getCreateTime().getTime());
            userRoleWrapper.clear();
        }
        return new PagingData<>(roleVOList, rolePage);
    }

    @Override
    public void createRole(RoleSaveDTO roleSaveDTO) {
        // 检查参数
        checkParam(roleSaveDTO);
        // 保存角色信息
        RolePO rolePO = CopyBeanUtil.copy(roleSaveDTO, RolePO.class);
        // 获取修改人信息
        Integer userId = ThreadLocalUtil.get();
        UserPO userPO = userMapper.selectById(userId);
        rolePO.setLastReviser(userPO.getUsername());
        // 设置角色编号
        rolePO.setRoleCode("r" + ((int)((Math.random() + 1) * 1000000)));
        roleMapper.insert(rolePO);
        List<Integer> permissionIdList = roleSaveDTO.getPermissionIdList();
        if(!CollectionUtils.isEmpty(permissionIdList)) {
            List<RolePermissionPO> rolePermissionList = getRolePermissionList(rolePO.getId(), permissionIdList);
            // 保存角色与权限信息（批量插入）
            if(!CollectionUtils.isEmpty(rolePermissionList)) {
                rolePermissionMapper.insertBatchSomeColumn(rolePermissionList);
            }
        }

        // 保存操作日志
        oplogService.saveOplog(OplogDto.builder()
                .operatePage("角色管理").operateType("新增")
                .targetType("角色").target(roleSaveDTO.getRoleName()).build()
        );
    }

    @Override
    public void deleteRoleById(Integer id) {
        RolePO rolePO = roleMapper.selectById(id);
        if(rolePO == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        // 检查该角色是否和用户绑定
        QueryWrapper<UserRolePO> userRoleMapperWrapper = new QueryWrapper<>();
        userRoleMapperWrapper.eq("role_id", id);
        if(userRoleMapper.selectCount(userRoleMapperWrapper) > 0) {
            throw new SecurityException(ResultCode.ROLE_USER_AUTHED);
        }
        // 删除角色与权限的关联
        QueryWrapper<RolePermissionPO> rolePermissionWrapper = new QueryWrapper<>();
        rolePermissionWrapper.eq("role_id", id);
        rolePermissionMapper.delete(rolePermissionWrapper);
        // 逻辑删除（自动）
        roleMapper.deleteById(rolePO.getId());

        // 保存操作日志
        oplogService.saveOplog(OplogDto.builder()
                .operatePage("角色管理").operateType("删除")
                .targetType("角色").target(rolePO.getRoleName()).build()
        );
    }

    @Override
    public void updateRoleById(RoleSaveDTO roleSaveDTO) {
        if(roleSaveDTO == null || roleMapper.selectById(roleSaveDTO.getId()) == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        checkParam(roleSaveDTO);
        // 更新角色基本信息
        RolePO rolePO = CopyBeanUtil.copy(roleSaveDTO, RolePO.class);
        // 设置修改人信息
        Integer userId = ThreadLocalUtil.get();
        UserPO userPO = userMapper.selectById(userId);
        rolePO.setLastReviser(userPO.getUsername());
        roleMapper.updateById(rolePO);
        // 先删除旧的角色与权限关联信息
        QueryWrapper<RolePermissionPO> rolePermissionWrapper = new QueryWrapper<>();
        rolePermissionWrapper.eq("role_id", rolePO.getId());
        rolePermissionMapper.delete(rolePermissionWrapper);
        // 更新角色与权限关联信息
        if(!CollectionUtils.isEmpty(roleSaveDTO.getPermissionIdList())) {
            List<RolePermissionPO> rpList = getRolePermissionList(rolePO.getId(), roleSaveDTO.getPermissionIdList());
            if(!CollectionUtils.isEmpty(rpList)) {
                rolePermissionMapper.insertBatchSomeColumn(rpList);
            }
        }

        // 保存操作日志
        oplogService.saveOplog(
                OplogDto.builder().operatePage("角色管理").operateType("编辑")
                .targetType("角色").target(roleSaveDTO.getRoleName()).build()
        );
    }

    @Override
    public void assignRoles(RoleAssignDTO roleAssignDTO) {
        checkParam(roleAssignDTO);
        // 获取old的用户与角色的关系
        List<Object> oldIdList = getOldRelation(roleAssignDTO);

        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        Integer id = roleAssignDTO.getId();
        List<UserRolePO> userRoleList = new ArrayList<>();
        if(roleAssignDTO.getFlag()) {
            // N个角色分配给1个用户
            if(id == null || userMapper.selectById(id) == null) {
                throw new SecurityException(ResultCode.USER_ACCOUNT_NOT_EXIST);
            }
            userRoleWrapper.eq("user_id", id);
            // 添加new角色用户关联信息
            for(Integer roleId : roleAssignDTO.getIdList()) {
                userRoleList.add(new UserRolePO(id, roleId));
            }
        } else {
            // 1个角色分配给N个用户
            if(id == null || roleMapper.selectById(id) == null) {
                throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
            }
            userRoleWrapper.eq("role_id", id);
            for(Integer userId : roleAssignDTO.getIdList()) {
                userRoleList.add(new UserRolePO(userId, id));
            }
        }
        // 删除old的全部角色用户关联信息
        userRoleMapper.delete(userRoleWrapper);
        // 插入new的角色与用户关联关系
        if(!CollectionUtils.isEmpty(userRoleList)) {
            userRoleMapper.insertBatchSomeColumn(userRoleList);
        }

        // 打包和保存角色更新消息（异步）
        ThreadPoolUtil.execute(() -> packAndSaveMessage(oldIdList, roleAssignDTO));

        // 保存操作日志
        if(roleAssignDTO.getFlag()) {
            UserPO userPO = userMapper.selectById(roleAssignDTO.getId());
            oplogService.saveOplog(OplogDto.builder()
                    .operatePage("用户管理").operateType("分配角色")
                    .targetType("用户").target(userPO.getUsername()).build()
            );
        } else {
            RolePO rolePO = roleMapper.selectById(roleAssignDTO.getId());
            oplogService.saveOplog(OplogDto.builder()
                    .operatePage("角色管理").operateType("分配用户")
                    .targetType("角色").target(rolePO.getRoleName()).build()
            );
        }
    }

    @Override
    public List<RoleBriefVO> listByRoleName(String roleName) {
        QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "role_name")
                .like(!StringUtils.isEmpty(roleName), "role_name", roleName);
        List<RolePO> roleList = roleMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(roleList, RoleBriefVO.class);
    }

    @Override
    public RoleDeleteCheckVO checkBeforeDelete(Integer roleId) {
        QueryWrapper<UserRolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id").eq("role_id", roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(queryWrapper);
        RoleDeleteCheckVO roleDeleteCheckVO = new RoleDeleteCheckVO();
        roleDeleteCheckVO.setRoleId(roleId);
        if(!CollectionUtils.isEmpty(userIdList)) {
            QueryWrapper<UserPO> userWrapper = new QueryWrapper<>();
            List<UserPO> userPOList = userMapper.selectList(userWrapper);
            List<String> usernameList = new ArrayList<>();
            for(UserPO userPo : userPOList) {
                usernameList.add(userPo.getUsername());
            }
            roleDeleteCheckVO.setUsernameNameList(usernameList);
        }
        return roleDeleteCheckVO;
    }

    private void checkParam(RoleAssignDTO roleAssignDTO) {
        if(roleAssignDTO.getFlag() == null) {
            throw new SecurityException(ResultCode.ROLE_ASSIGN_FLAG_IS_NULL);
        }
    }

    @Override
    public List<AssignDataDTO> getAssignDataByRoleId(Integer roleId, String name) {
        if(roleId == null) {
            throw new SecurityException(ResultCode.ROLE_ID_CANNOT_BE_NULL);
        }
        // 查询所有的用户，并根据用户添加时间排序（倒序）
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "real_name")
                .like(!StringUtils.isEmpty(name), "username", name)
                .like(!StringUtils.isEmpty(name), "real_name", name)
                .orderByDesc("create_time");
        List<UserPO> userList = userMapper.selectList(queryWrapper);

        // 先获取该角色已分配的用户，并转为set
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.select("user_id").eq("role_id", roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(userRoleWrapper);
        Set<Object> hasRoleUserIdSet = new HashSet<>(userIdList);

        // 封装List<AssignDataVo>
        List<AssignDataDTO> list = new ArrayList<>();
        for(UserPO userPO : userList) {
            AssignDataDTO data = new AssignDataDTO();
            // 判断用户是否拥有该角色
            data.setHas(hasRoleUserIdSet.contains(userPO.getId()));
            data.setName(userPO.getUsername() + "/" + userPO.getRealName());
            data.setId(userPO.getId());
            list.add(data);
        }
        return list;
    }

    /**
     * 记录old的角色与用户的关系
     * @param roleAssignDTO 条件
     * @return old用户拥有的角色idList，或old角色已分配的用户idList
     */
    private List<Object> getOldRelation(RoleAssignDTO roleAssignDTO) {
        // 获取传入方法的参数
        QueryWrapper<UserRolePO> userRoleWrapper = new QueryWrapper<>();
        if (roleAssignDTO.getFlag()) {
            // 如果是N个角色分配给1个用户
            userRoleWrapper.select("role_id").eq("user_id", roleAssignDTO.getId());
        } else {
            // 1个角色分配给N个用户
            userRoleWrapper.select("user_id").eq("role_id", roleAssignDTO.getId());
        }
        // 获取old用户拥有的角色idList，或获取old角色已分配的用户idList
        return userRoleMapper.selectObjs(userRoleWrapper);
    }

    private void packAndSaveMessage(List<Object> oldIdList, RoleAssignDTO roleAssignDTO) {
        List<Integer> newIdList = roleAssignDTO.getIdList();

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

        if (roleAssignDTO.getFlag()) {
            // 如果是N个角色分配给1个用户，oldIdList和newIdList都为角色idList
            Integer userId= roleAssignDTO.getId();
            // 保存移除角色消息
            saveRoleAssignMessage(userId, removeIdList, MessageCode.ROLE_REMOVE_MESSAGE);
            // 保存新增角色消息
            saveRoleAssignMessage(userId, addIdList, MessageCode.ROLE_ADD_MESSAGE);
        } else {
            // 1个角色分配给N个用户，oldIdList和newIdList都为用户idList
            List<Integer> roleIdList = new ArrayList<>();
            roleIdList.add(roleAssignDTO.getId());
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
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
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
        messageService.saveMessage(messageDto);
    }

    /**
     * 添加或者修改时候检查参数
     * @param roleSaveDTO 角色信息
     */
    private void checkParam(RoleSaveDTO roleSaveDTO) {
        if(StringUtils.isEmpty(roleSaveDTO.getRoleName())) {
            throw new SecurityException(ResultCode.ROLE_NAME_CANNOT_BE_BLANK);
        }
        if(StringUtils.isEmpty(roleSaveDTO.getDescription())) {
            // TODO;
            throw new SecurityException(ResultCode.ROLE_DEPT_CANNOT_BE_BLANK);
        }
        if(CollectionUtils.isEmpty(roleSaveDTO.getPermissionIdList())) {
            throw new SecurityException(ResultCode.ROLE_PERMISSION_CANNOT_BE_NULL);
        }
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        roleWrapper
                .eq("role_name", roleSaveDTO.getRoleName())
                // 如果有id信息，说明是更新，则判断角色名重复的时候要排除old信息
                .ne(roleSaveDTO.getId() != null, "id", roleSaveDTO.getId());
        if(roleMapper.selectCount(roleWrapper) > 0) {
            throw new SecurityException(ResultCode.ROLE_NAME_EXIST);
        }
    }

    /**
     * 用于构建可以直接插入角色与权限中间表的数据
     * @param roleId 角色Id
     * @param permissionIdList 权限idList
     * @return List<RolePermissionPO>
     */
    private List<RolePermissionPO> getRolePermissionList(Integer roleId, List<Integer> permissionIdList) {
        List<RolePermissionPO> rolePermissionList = new ArrayList<>();
        for(Integer permissionId : permissionIdList) {
            RolePermissionPO rolePermissionPO = new RolePermissionPO();
            rolePermissionPO.setRoleId(roleId);
            rolePermissionPO.setPermissionId(permissionId);
            rolePermissionList.add(rolePermissionPO);
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