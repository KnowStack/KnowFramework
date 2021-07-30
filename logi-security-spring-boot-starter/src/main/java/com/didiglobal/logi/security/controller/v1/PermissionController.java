package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.permission.PermissionVo;
import com.didiglobal.logi.security.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cjm
 */
@RestController
@Api(value = "permission相关API接口", tags = "权限相关API接口")
@RequestMapping("/v1/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/tree")
    @ApiOperation(value = "获取所有权限", notes = "以树的形式返回所有权限")
    public Result<PermissionVo> tree() {
        PermissionVo permissionVo = permissionService.buildPermissionTree();
        return Result.success(permissionVo);
    }
}
