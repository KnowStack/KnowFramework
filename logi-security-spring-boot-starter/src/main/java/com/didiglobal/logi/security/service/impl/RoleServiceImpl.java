package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.role.AssignInfoVO;
import com.didiglobal.logi.security.common.dto.role.RoleAssignDTO;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.dto.role.RoleSaveDTO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.role.RoleDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import com.didiglobal.logi.security.common.dto.message.MessageDTO;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.enums.message.MessageCode;
import com.didiglobal.logi.security.common.po.RolePO;
import com.didiglobal.logi.security.common.vo.permission.PermissionTreeVO;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.*;
import com.didiglobal.logi.security.service.*;

import java.text.SimpleDateFormat;
import java.util.*;

import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.MathUtil;
import com.didiglobal.logi.security.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private OplogService oplogService;

    @Autowired
    private UserService userService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public RoleVO getRoleDetailByRoleId(Integer roleId) {
        RolePO rolePO = roleMapper.selectById(roleId);
        if(rolePO == null) {
            return null;
        }
        // 根据角色id去查权限树
        PermissionTreeVO permissionTreeVO = permissionService.buildPermissionTreeByRoleId(rolePO.getId());
        RoleVO roleVo = CopyBeanUtil.copy(rolePO, RoleVO.class);
        roleVo.setPermissionTreeVO(permissionTreeVO);
        roleVo.setCreateTime(rolePO.getCreateTime().getTime());
        // 获取授予用户数
        List<Integer> userIdList = userRoleService.getUserIdListByRoleId(roleId);
        roleVo.setAuthedUserCnt(userIdList.size());
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

        List<RoleVO> roleVOList = new ArrayList<>();
        for(RolePO rolePO : rolePage.getRecords()) {
            RoleVO roleVO = CopyBeanUtil.copy(rolePO, RoleVO.class);
            // 获取该角色已分配给的用户数
            List<Integer> userIdList = userRoleService.getUserIdListByRoleId(rolePO.getId());
            roleVO.setAuthedUserCnt(userIdList.size());
            roleVO.setCreateTime(rolePO.getCreateTime().getTime());
            roleVOList.add(roleVO);
        }
        return new PagingData<>(roleVOList, rolePage);
    }

    @Override
    public void createRoleWithUserId(Integer userId, RoleSaveDTO roleSaveDTO) throws SecurityException {
        // 检查参数
        checkParam(roleSaveDTO, false);
        // 保存角色信息
        RolePO rolePO = CopyBeanUtil.copy(roleSaveDTO, RolePO.class);
        // 设置修改人信息
        UserBriefVO userBriefVO = userService.getUserBriefByUserId(userId);
        if(userBriefVO != null) {
            rolePO.setLastReviser(userBriefVO.getUsername());
        }
        // 设置角色编号
        rolePO.setRoleCode("r" + ((int)((Math.random() + 1) * 1000000)));
        roleMapper.insert(rolePO);
        // 保持角色与权限的关联信息
        rolePermissionService.saveRolePermission(rolePO.getId(), roleSaveDTO.getPermissionIdList());
        // 保存操作日志
        OplogDTO oplogDTO = new OplogDTO("角色管理", "新增", "角色", roleSaveDTO.getRoleName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public void deleteRoleByRoleId(Integer roleId) {
        RolePO rolePO = roleMapper.selectById(roleId);
        if(rolePO == null) {
            return;
        }
        // 检查该角色是否和用户绑定
        List<Integer> userIdList = userRoleService.getUserIdListByRoleId(roleId);
        if(userIdList.size() > 0) {
            throw new SecurityException(ResultCode.ROLE_USER_AUTHED);
        }
        // 删除角色与权限的关联
        rolePermissionService.deleteRolePermissionByRoleId(roleId);
        // 逻辑删除（自动）
        roleMapper.deleteById(roleId);
        // 保存操作日志
        OplogDTO oplogDTO = new OplogDTO("角色管理", "删除", "角色", rolePO.getRoleName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public void updateRoleWithUserId(Integer userId, RoleSaveDTO roleSaveDTO) {
        if(roleMapper.selectById(roleSaveDTO.getId()) == null) {
            throw new SecurityException(ResultCode.ROLE_NOT_EXISTS);
        }
        checkParam(roleSaveDTO, true);
        // 更新角色基本信息
        RolePO rolePO = CopyBeanUtil.copy(roleSaveDTO, RolePO.class);
        // 设置修改人信息
        UserBriefVO userBriefVO = userService.getUserBriefByUserId(userId);
        if(userBriefVO != null) {
            rolePO.setLastReviser(userBriefVO.getUsername());
        }
        roleMapper.updateById(rolePO);
        // 更新角色与权限关联信息
        rolePermissionService.updateRolePermission(rolePO.getId(), roleSaveDTO.getPermissionIdList());
        // 保存操作日志
        OplogDTO oplogDTO = new OplogDTO("角色管理", "编辑", "角色", roleSaveDTO.getRoleName());
        oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
    }

    @Override
    public void assignRoles(RoleAssignDTO roleAssignDTO) throws SecurityException {
        if(roleAssignDTO.getFlag() == null) {
            throw new SecurityException(ResultCode.ROLE_ASSIGN_FLAG_IS_NULL);
        }
        if(roleAssignDTO.getFlag()) {
            // N个角色分配给1个用户
            Integer userId = roleAssignDTO.getId();
            // 获取old的用户与角色的关系
            List<Integer> oldRoleIdList = userRoleService.getRoleIdListByUserId(userId);
            // 更新关联信息
            userRoleService.updateUserRoleByUserId(userId, roleAssignDTO.getIdList());
            // 保存操作日志
            UserBriefVO userBriefVO = userService.getUserBriefByUserId(roleAssignDTO.getId());
            OplogDTO oplogDTO = new OplogDTO("用户管理", "分配角色", "用户", userBriefVO.getUsername());
            Integer oplogId = oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
            // 打包和保存角色更新消息
            packAndSaveMessage(oplogId, oldRoleIdList, roleAssignDTO);
        } else {
            // 1个角色分配给N个用户
            Integer roleId = roleAssignDTO.getId();
            // 获取old的用户与角色的关系
            List<Integer> oldUserIdList = userRoleService.getUserIdListByRoleId(roleId);
            userRoleService.updateUserRoleByRoleId(roleId, roleAssignDTO.getIdList());
            // 保存操作日志
            RolePO rolePO = roleMapper.selectById(roleAssignDTO.getId());
            OplogDTO oplogDTO = new OplogDTO("角色管理", "分配用户", "角色", rolePO.getRoleName());
            Integer oplogId = oplogService.saveOplogWithUserId(ThreadLocalUtil.get(), oplogDTO);
            // 打包和保存角色更新消息
            packAndSaveMessage(oplogId, oldUserIdList, roleAssignDTO);
        }
    }

    @Override
    public List<RoleBriefVO> getRoleBriefListByRoleName(String roleName) {
        QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "role_name")
                .like(!StringUtils.isEmpty(roleName), "role_name", roleName)
                // 据角色添加时间排序（倒序）
                .orderByDesc("create_time");
        List<RolePO> roleList = roleMapper.selectList(queryWrapper);
        return CopyBeanUtil.copyList(roleList, RoleBriefVO.class);
    }

    @Override
    public RoleDeleteCheckVO checkBeforeDelete(Integer roleId) {
        if(roleId == null) {
            return null;
        }
        RoleDeleteCheckVO roleDeleteCheckVO = new RoleDeleteCheckVO();
        roleDeleteCheckVO.setRoleId(roleId);
        List<Integer> userIdList = userRoleService.getUserIdListByRoleId(roleId);
        if(!CollectionUtils.isEmpty(userIdList)) {
            List<UserBriefVO> userBriefVOList = userService.getUserBriefListByUserIdList(userIdList);
            List<String> usernameList = new ArrayList<>();
            for(UserBriefVO userBriefVO : userBriefVOList) {
                usernameList.add(userBriefVO.getUsername());
            }
            roleDeleteCheckVO.setUsernameList(usernameList);
        }
        return roleDeleteCheckVO;
    }

    @Override
    public List<RoleBriefVO> getAllRoleBriefList() {
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        roleWrapper.select("id", "role_name");
        List<RolePO> rolePOList = roleMapper.selectList(roleWrapper);
        return CopyBeanUtil.copyList(rolePOList, RoleBriefVO.class);
    }

    @Override
    public List<RoleBriefVO> getRoleBriefListByUserId(Integer userId) {
        // 查询用户关联的角色
        List<Integer> roleIdList = userRoleService.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        roleWrapper.select("id", "role_name").in("id", roleIdList);
        List<RolePO> rolePOList = roleMapper.selectList(roleWrapper);
        return CopyBeanUtil.copyList(rolePOList, RoleBriefVO.class);
    }

    @Override
    public List<AssignInfoVO> getAssignInfoByRoleId(Integer roleId, String name) {
        if(roleId == null) {
            return null;
        }
        // 获取用户List
        List<UserBriefVO> userBriefVOList = userService.getUserBriefListByUsernameOrRealName(name);

        // 先获取该角色已分配的用户，并转为set
        List<Integer> userIdList = userRoleService.getUserIdListByRoleId(roleId);
        Set<Integer> hasRoleUserIdSet = new HashSet<>(userIdList);

        // 封装List<AssignDataVo>
        List<AssignInfoVO> result = new ArrayList<>();
        for(UserBriefVO userBriefVO : userBriefVOList) {
            AssignInfoVO assignInfoVO = new AssignInfoVO();
            // 判断用户是否拥有该角色
            assignInfoVO.setHas(hasRoleUserIdSet.contains(userBriefVO.getId()));
            assignInfoVO.setName(userBriefVO.getUsername() + "/" + userBriefVO.getRealName());
            assignInfoVO.setId(userBriefVO.getId());
            result.add(assignInfoVO);
        }
        return result;
    }

    private void packAndSaveMessage(Integer oplogId, List<Integer> oldIdList, RoleAssignDTO roleAssignDTO) {
        List<Integer> newIdList = roleAssignDTO.getIdList();

        List<Integer> removeIdList = new ArrayList<>();
        List<Integer> addIdList = new ArrayList<>();
        // 取交集
        Set<Integer> set = MathUtil.getIntersection(oldIdList, newIdList);
        for(Integer oldId : oldIdList) {
            if(!set.contains(oldId)) {
                removeIdList.add(oldId);
            }
        }
        for(Integer newId : newIdList) {
            if(!set.contains(newId)) {
                addIdList.add(newId);
            }
        }

        if (roleAssignDTO.getFlag()) {
            // 如果是N个角色分配给1个用户，oldIdList和newIdList都为角色idList
            List<Integer> userIdList = new ArrayList<>();
            userIdList.add(roleAssignDTO.getId());
            // 保存移除角色消息 和 新增角色消息
            saveRoleAssignMessage(oplogId, userIdList, removeIdList, userIdList, addIdList);
        } else {
            // 1个角色分配给N个用户，oldIdList和newIdList都为用户idList
            List<Integer> roleIdList = new ArrayList<>();
            roleIdList.add(roleAssignDTO.getId());
            // 保存移除角色消息 和 新增角色消息
            saveRoleAssignMessage(oplogId, removeIdList, roleIdList, addIdList, roleIdList);
        }
    }

    /**
     * 保存用户角色变更消息
     * @param oplogId 操作日志id
     * @param removeUserIdList 被移除角色的用户idList
     * @param removeRoleIdList 移除的角色idList
     * @param addUserIdList 新增角色的用户idList
     * @param addRoleIdList 新增的角色idList
     */
    private void saveRoleAssignMessage(Integer oplogId,
                                       List<Integer> removeUserIdList, List<Integer> removeRoleIdList,
                                       List<Integer> addUserIdList, List<Integer> addRoleIdList) {
        // 获取当前时间
        SimpleDateFormat formatter= new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);

        // 拼接变更的角色信息
        String addRoleInfo = spliceRoleNameByRoleIdList(addRoleIdList);
        String removeRoleInfo = spliceRoleNameByRoleIdList(removeRoleIdList);

        List<MessageDTO> messageDTOList = new ArrayList<>();
        for(Integer userId : addUserIdList) {
            MessageDTO messageDTO = new MessageDTO(userId, oplogId);
            // 赋值占位符
            String content = String.format(MessageCode.ROLE_ADD_MESSAGE.getContent(), time, addRoleInfo);
            messageDTO.setContent(content);
            messageDTO.setTitle(MessageCode.ROLE_ADD_MESSAGE.getTitle());
            messageDTOList.add(messageDTO);
        }
        for(Integer userId : removeUserIdList) {
            MessageDTO messageDTO = new MessageDTO(userId, oplogId);
            // 赋值占位符
            String content = String.format(MessageCode.ROLE_REMOVE_MESSAGE.getContent(), time, removeRoleInfo);
            messageDTO.setContent(content);
            messageDTO.setTitle(MessageCode.ROLE_REMOVE_MESSAGE.getTitle());
            messageDTOList.add(messageDTO);
        }
        messageService.saveMessages(messageDTOList);
    }

    private String spliceRoleNameByRoleIdList(List<Integer> roleIdList) {
        // 根据角色id查询
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        roleWrapper.select("role_name").in("id", roleIdList);
        List<Object> roleNameList = roleMapper.selectObjs(roleWrapper);

        // 拼接角色信息
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < roleNameList.size() - 1; i++) {
            sb.append(roleNameList.get(i)).append(",");
        }
        sb.append(roleNameList.get(roleNameList.size() - 1));
        return sb.toString();
    }

    /**
     * 添加或者修改时候检查参数
     * @param roleSaveDTO 角色信息
     * @param isUpdate 是创建还是更新
     * @throws SecurityException 校验错误
     */
    private void checkParam(RoleSaveDTO roleSaveDTO, boolean isUpdate) throws SecurityException {
        if(StringUtils.isEmpty(roleSaveDTO.getRoleName())) {
            throw new SecurityException(ResultCode.ROLE_NAME_CANNOT_BE_BLANK);
        }
        if(StringUtils.isEmpty(roleSaveDTO.getDescription())) {
            throw new SecurityException(ResultCode.ROLE_DEPT_CANNOT_BE_BLANK);
        }
        if(CollectionUtils.isEmpty(roleSaveDTO.getPermissionIdList())) {
            throw new SecurityException(ResultCode.ROLE_PERMISSION_CANNOT_BE_NULL);
        }
        QueryWrapper<RolePO> roleWrapper = new QueryWrapper<>();
        roleWrapper.eq("role_name", roleSaveDTO.getRoleName());
        if(isUpdate) {
            // 如果是更新操作，则判断角色名重复的时候要排除old信息
            roleWrapper.ne("id", roleSaveDTO.getId());
        }
        if(roleMapper.selectCount(roleWrapper) > 0) {
            throw new SecurityException(ResultCode.ROLE_NAME_ALREADY_EXISTS);
        }
    }
}