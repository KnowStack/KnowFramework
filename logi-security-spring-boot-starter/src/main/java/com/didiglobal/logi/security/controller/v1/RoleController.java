package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.role.AssignDataDTO;
import com.didiglobal.logi.security.common.dto.role.RoleAssignDTO;
import com.didiglobal.logi.security.common.dto.role.RoleQueryDTO;
import com.didiglobal.logi.security.common.dto.role.RoleSaveDTO;
import com.didiglobal.logi.security.common.vo.role.RoleBriefVO;
import com.didiglobal.logi.security.common.vo.role.RoleDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import com.didiglobal.logi.security.service.RoleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "role相关API接口", tags = "角色相关API接口")
@RequestMapping("/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    @ApiOperation(value = "获取角色详情", notes = "根据角色id或角色code获取角色详情")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<RoleVO> detail(@PathVariable Integer id) {
        RoleVO roleVo = roleService.getDetailById(id);
        return Result.success(roleVo);
    }

    @PutMapping
    @ApiOperation(value = "更新角色信息", notes = "根据角色id更新角色信息")
    public Result<String> update(@RequestBody RoleSaveDTO roleSaveDTO) {
        roleService.updateRoleById(roleSaveDTO);
        return Result.success();
    }

    @PostMapping
    @ApiOperation(value = "创建角色", notes = "创建角色")
    public Result<String> create(@RequestBody RoleSaveDTO roleSaveDTO) {
        roleService.createRole(roleSaveDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/check/{id}")
    @ApiOperation(value = "删除角色前的检查", notes = "判断该角色是否已经分配给用户，如有分配给用户，则返回用户的信息list")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<RoleDeleteCheckVO> check(@PathVariable Integer id) {
        RoleDeleteCheckVO deleteCheckVO = roleService.checkBeforeDelete(id);
        return Result.success(deleteCheckVO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除角色", notes = "根据角色id删除角色")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<String> delete(@PathVariable Integer id) {
        roleService.deleteRoleById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页查询角色列表", notes = "分页和条件查询")
    public PagingResult<RoleVO> page(@RequestBody RoleQueryDTO queryVo) {
        PagingData<RoleVO> pageRole = roleService.getRolePage(queryVo);
        return PagingResult.success(pageRole);
    }

    @PostMapping("/assign")
    @ApiOperation(value = "分配角色", notes = "分配一个角色给多个用户或分配多个角色给一个用户")
    public Result<String> assign(@RequestBody RoleAssignDTO assignVo) {
        roleService.assignRoles(assignVo);
        return Result.success();
    }

    @GetMapping(value = {"/assign/list/{roleId}/{name}", "/assign/list/{roleId}"})
    @ApiOperation(value = "角色管理/分配用户/列表", notes = "根据角色id和用户实名或账户名模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "int", required = true),
            @ApiImplicitParam(name = "name", value = "用户实名或账户名（为null，则获取全部用户）", dataType = "String", required = false),
    })
    public Result<List<AssignDataDTO>> assignList(@PathVariable(value = "roleId", required = true) Integer roleId,
                                                  @PathVariable(value = "name", required = false) String name) {
        List<AssignDataDTO> assignDataDTOList = roleService.getAssignDataByRoleId(roleId, name);
        return Result.success(assignDataDTOList);
    }

    @GetMapping(value = {"/list/{roleName}", "/list"})
    @ApiOperation(value = "根据角色名模糊查询", notes = "用户管理/列表查询条件/分配角色框，这里会用到此接口")
    @ApiImplicitParam(name = "roleName", value = "角色名（为null，查询全部）", dataType = "String", required = false)
    public Result<List<RoleBriefVO>> list(@PathVariable(required = false) String roleName) {
        List<RoleBriefVO> roleBriefVOList = roleService.listByRoleName(roleName);
        return Result.success(roleBriefVOList);
    }
}