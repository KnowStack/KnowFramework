package com.didiglobal.logi.security.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.role.RoleAssignVo;
import com.didiglobal.logi.security.common.vo.role.RoleQueryVo;
import com.didiglobal.logi.security.common.vo.role.RoleSaveVo;
import com.didiglobal.logi.security.common.vo.role.RoleVo;
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
    public Result<RoleVo> detail(@PathVariable Integer id) {
        RoleVo roleVo = roleService.getDetailById(id);
        return Result.success(roleVo);
    }

    @PutMapping
    @ApiOperation(value = "更新角色信息", notes = "根据角色id更新角色信息")
    public Result<String> update(@RequestBody RoleSaveVo roleSaveVo) {
        roleService.updateRoleById(roleSaveVo);
        return Result.success();
    }

    @PostMapping
    @ApiOperation(value = "创建角色", notes = "创建角色")
    public Result<String> create(@RequestBody RoleSaveVo roleSaveVo) {
        roleService.createRole(roleSaveVo);
        return Result.success();
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
    public PagingResult<RoleVo> page(@RequestBody RoleQueryVo queryVo) {
        PagingData<RoleVo> pageRole = roleService.getRolePage(queryVo);
        return PagingResult.success(pageRole);
    }

    @PostMapping("/assign")
    @ApiOperation(value = "分配角色", notes = "分配一个角色给多个用户或分配多个角色给一个用户")
    public Result<String> assign(@RequestBody RoleAssignVo assignVo) {
        roleService.assignRoles(assignVo);
        return Result.success();
    }

    @GetMapping("/list/{roleName}")
    @ApiOperation(value = "根据角色名查询", notes = "根据角色名模糊查询")
    @ApiImplicitParam(name = "roleName", value = "角色名", dataType = "String", required = true)
    public Result<List<RoleVo>> list(@PathVariable String roleName) {
        List<RoleVo> roleVoList = roleService.listByRoleName(roleName);
        return Result.success(roleVoList);
    }
}