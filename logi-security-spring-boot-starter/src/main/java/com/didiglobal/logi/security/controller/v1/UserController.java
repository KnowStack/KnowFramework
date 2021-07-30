package com.didiglobal.logi.security.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.user.UserQueryVo;
import com.didiglobal.logi.security.common.vo.user.UserVo;
import com.didiglobal.logi.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public Result<UserVo> get(@PathVariable Integer id) {
        UserVo userVo = userService.getDetailById(id);
        return Result.success(userVo);
    }

    @PostMapping("/page")
    @ApiOperation(value = "查询用户列表", notes = "分页和条件查询")
    public PagingResult<UserVo> page(UserQueryVo queryVo) {
        IPage<UserVo> pageUser = userService.getPageUser(queryVo);
        return PagingResult.success(pageUser);
    }
}

