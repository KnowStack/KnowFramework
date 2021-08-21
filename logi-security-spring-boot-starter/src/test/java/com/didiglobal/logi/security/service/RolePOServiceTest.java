package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.dto.role.RoleAssignDTO;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.dto.role.RoleSaveDTO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
public class RolePOServiceTest extends BaseTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void testAssignRoles() {
        RoleAssignDTO roleAssignDTO = new RoleAssignDTO();
        // 角色
        roleAssignDTO.setId(5);
        roleAssignDTO.setFlag(false);
        List<Integer> list = new ArrayList<>();
        list.add(6);
        list.add(7);
        roleAssignDTO.setIdList(list);
        roleService.assignRoles(roleAssignDTO);
    }

    @Test
    public void testDeleteRoleById() {
        roleService.deleteRoleByRoleId(7);
    }

    @Test
    public void testGetDetailByIdOrCode() {
        int roleId = 2;
        String roleCode = "role11608";
        RoleVO roleVo = roleService.getRoleDetailByRoleId(2);
        System.out.println(roleVo);
    }

    @Test
    public void testCreateRole() {
        RoleSaveDTO roleSaveDTO = new RoleSaveDTO();
        roleSaveDTO.setRoleName("日志管理员");
        roleSaveDTO.setDescription("这是日志管理员角色");
        List<Integer> permissionIdList = new ArrayList<>();
        permissionIdList.add(3);
        permissionIdList.add(16);
        roleSaveDTO.setPermissionIdList(permissionIdList);
        roleService.createRole(roleSaveDTO);
    }

    @Test
    public void testGetPageRole() {
        RoleQueryDTO queryVo = new RoleQueryDTO();
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
        RoleSaveDTO roleSaveDTO = new RoleSaveDTO();
        roleSaveDTO.setId(2);
        roleSaveDTO.setDescription("这是管理员角色123");
        roleService.updateRoleByRoleId(roleSaveDTO);
    }
}
