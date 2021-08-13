package com.didiglobal.logi.security.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.entity.BaseEntity;
import com.didiglobal.logi.security.common.entity.User;
import com.didiglobal.logi.security.common.vo.role.AssignDataVo;
import com.didiglobal.logi.security.common.vo.user.UserQueryVo;
import com.didiglobal.logi.security.common.vo.user.UserVo;
import com.didiglobal.logi.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public Result<UserVo> detail(@PathVariable Integer id) {
        UserVo userVo = userService.getDetailById(id);
        return Result.success(userVo);
    }

    @PostMapping("/page")
    @ApiOperation(value = "查询用户列表", notes = "分页和条件查询")
    public PagingResult<UserVo> page(@RequestBody UserQueryVo queryVo) {
        PagingData<UserVo> pageUser = userService.getUserPage(queryVo);
        return PagingResult.success(pageUser);
    }

    @GetMapping("/list/dept/{deptId}")
    @ApiOperation(value = "根据部门id获取用户list", notes = "根据部门id获取用户list")
    @ApiImplicitParam(name = "deptId", value = "部门id", dataType = "int", required = true)
    public Result<List<UserVo>> listByDeptId(@PathVariable Integer deptId) {
        List<UserVo> userVoList = userService.getListByDeptId(deptId);
        return Result.success(userVoList);
    }

    @GetMapping("/assign/list")
    @ApiOperation(value = "用户管理/分配角色/列表", notes = "根据用户id和角色名模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "int", required = true),
            @ApiImplicitParam(name = "roleName", value = "角色名", dataType = "String", required = false),
    })
    public Result<List<AssignDataVo>> list(@RequestParam(value = "userId", required = true) Integer userId,
                                           @RequestParam(value = "roleName", required = false) String roleName) {
        List<AssignDataVo> assignDataVoList = userService.getAssignDataByUserId(userId, roleName);
        return Result.success(assignDataVoList);
    }
}

