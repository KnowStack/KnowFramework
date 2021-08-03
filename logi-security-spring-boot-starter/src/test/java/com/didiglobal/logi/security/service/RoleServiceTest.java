package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.vo.role.RoleAssignVo;
import com.didiglobal.logi.security.common.vo.role.RoleQueryVo;
import com.didiglobal.logi.security.common.vo.role.RoleSaveVo;
import com.didiglobal.logi.security.common.vo.role.RoleVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
public class RoleServiceTest extends BaseTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void testAssignRoles() {
        RoleAssignVo roleAssignVo = new RoleAssignVo();
        // 角色
        roleAssignVo.setId(5);
        roleAssignVo.setFlag(false);
        List<Integer> list = new ArrayList<>();
        list.add(6);
        list.add(7);
        roleAssignVo.setIdList(list);
        roleService.assignRoles(roleAssignVo);
    }

    @Test
    public void testDeleteRoleById() {
        roleService.deleteRoleById(7);
    }

    @Test
    public void testGetDetailByIdOrCode() {
        int roleId = 2;
        String roleCode = "role11608";
        RoleVo roleVo = roleService.getDetailById(2);
        System.out.println(roleVo);
    }

    @Test
    public void testCreateRole() {
        RoleSaveVo roleSaveVo = new RoleSaveVo();
        roleSaveVo.setRoleName("日志管理员");
        roleSaveVo.setDescription("这是日志管理员角色");
        List<Integer> permissionIdList = new ArrayList<>();
        permissionIdList.add(3);
        permissionIdList.add(16);
        roleSaveVo.setPermissionIdList(permissionIdList);
        roleService.createRole(roleSaveVo);
    }

    @Test
    public void testGetPageRole() {
        RoleQueryVo queryVo = new RoleQueryVo();
        queryVo.setPage(1);
        queryVo.setSize(3);
        queryVo.setRoleName("管");
        queryVo.setRoleCode("role11608");
        // IPage<RoleVo> pageRole = roleService.getPageRole(queryVo);
        // System.out.println(pageRole);
    }

    @Test
    public void testUpdateRoleById() {
        int roleId = 2;
        RoleSaveVo roleSaveVo = new RoleSaveVo();
        roleSaveVo.setId(2);
        roleSaveVo.setDescription("这是管理员角色123");
        roleService.updateRoleById(roleSaveVo);
    }
}
