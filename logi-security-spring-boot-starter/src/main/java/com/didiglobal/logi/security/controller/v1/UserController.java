package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.role.AssignDataDTO;
import com.didiglobal.logi.security.common.dto.user.UserQueryDTO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.common.vo.user.UserVO;
import com.didiglobal.logi.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "user相关API接口", tags = "用户相关API接口")
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户详情", notes = "根据用户id获取用户详情")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "int", required = true)
    public Result<UserVO> detail(@PathVariable Integer id) {
        UserVO userVo = userService.getDetailById(id);
        return Result.success(userVo);
    }

    @PostMapping("/page")
    @ApiOperation(value = "查询用户列表", notes = "分页和条件查询")
    public PagingResult<UserVO> page(@RequestBody UserQueryDTO queryVo) {
        PagingData<UserVO> pageUser = userService.getUserPage(queryVo);
        return PagingResult.success(pageUser);
    }

    @GetMapping("/list/dept/{deptId}")
    @ApiOperation(value = "根据部门id获取用户list", notes = "根据部门id获取用户简要信息list")
    @ApiImplicitParam(name = "deptId", value = "部门id", dataType = "int", required = true)
    public Result<List<UserBriefVO>> listByDeptId(@PathVariable Integer deptId) {
        List<UserBriefVO> userBriefVOList = userService.getListByDeptId(deptId);
        return Result.success(userBriefVOList);
    }

    @GetMapping("/list/role/{roleId}")
    @ApiOperation(value = "根据角色id获取用户list", notes = "根据角色id获取用户简要信息list")
    @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "int", required = true)
    public Result<List<UserBriefVO>> listByRoleId(@PathVariable Integer roleId) {
        List<UserBriefVO> userBriefVOList = userService.getListByRoleId(roleId);
        return Result.success(userBriefVOList);
    }

    @GetMapping(value = {"/assign/list/{userId}/{roleName}", "/assign/list/{userId}"})
    @ApiOperation(value = "用户管理/分配角色/列表", notes = "根据用户id和角色名模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "int", required = true),
            @ApiImplicitParam(name = "roleName", value = "角色名（为null，则获取全部角色）", dataType = "String", required = false),
    })
    public Result<List<AssignDataDTO>> assignList(@PathVariable(value = "userId", required = true) Integer userId,
                                                  @PathVariable(value = "roleName", required = false) String roleName) {
        List<AssignDataDTO> assignDataDTOList = userService.getAssignDataByUserId(userId, roleName);
        return Result.success(assignDataDTOList);
    }

    @GetMapping(value = {"/list/{name}", "/list"})
    @ApiOperation(value = "根据账户名或用户实名查询", notes = "获取用户简要信息list，会分别以账户名和实名去模糊查询，返回两者的并集")
    @ApiImplicitParam(name = "name", value = "账户名或用户实名（为null，则获取全部用户）", dataType = "String", required = false)
    public Result<List<UserBriefVO>> listByName(@PathVariable(required = false) String name) {
        List<UserBriefVO> userBriefVOList = userService.getListByUsernameOrRealName(name);
        return Result.success(userBriefVOList);
    }
}

