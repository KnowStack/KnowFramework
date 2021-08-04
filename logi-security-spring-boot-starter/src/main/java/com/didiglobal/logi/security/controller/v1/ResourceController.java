package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.resource.*;
import com.didiglobal.logi.security.service.ResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "resource相关API接口", tags = "资源相关API接口")
@RequestMapping("/v1/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/type/list")
    @ApiOperation(value = "获取所有资源类型", notes = "获取所有资源类型")
    public Result<List<ResourceTypeVo>> typeList() {
        List<ResourceTypeVo> resourceTypeVoList = resourceService.getResourceTypeList();
        return Result.success(resourceTypeVoList);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取具体资源list", notes = "根据项目id和资源类别id获取具体资源list（两个id都为null，则获取所有资源）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = false),
            @ApiImplicitParam(name = "resourceTypeId", value = "资源类别id", required = false)
    })
    public Result<List<ResourceVo>> typeList(@RequestParam Integer projectId, @RequestParam Integer resourceTypeId) {
        List<ResourceVo> resourceVoList = resourceService.getResourceList(projectId, resourceTypeId);
        return Result.success(resourceVoList);
    }

    @PostMapping("/page/mbr")
    @ApiOperation(value = "资源权限管理（按资源管理的列表信息）", notes = "资源权限管理（按资源管理的列表信息），接口中mbr就是ManageByResource")
    public PagingResult<ManageByResourceVo> page(@RequestBody ManageByResourceQueryVo queryVo) {
        PagingData<ManageByResourceVo> pagingData = resourceService.getManageByResourcePage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/page/mbu")
    @ApiOperation(value = "资源权限管理（按用户管理的列表信息）", notes = "资源权限管理（按用户管理的列表信息），接口中mbu就是ManageByUser")
    public PagingResult<ManageByUserVo> page(@RequestBody ManageByUserQueryVo queryVo) {
        PagingData<ManageByUserVo> pagingData = resourceService.getManageByUserPage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/assign/mbr")
    @ApiOperation(value = "按资源管理（分配用户）", notes = "资源权限管理（按资源管理（分配用户））")
    public Result<String> assign(@RequestBody AssignToManyUserVo assignToManyUserVo) {
        resourceService.assignResourcePermission(assignToManyUserVo);
        return Result.success();
    }

    @PostMapping("/assign/mbu")
    @ApiOperation(value = "按用户管理（分配资源）", notes = "资源权限管理（按用户管理（分配资源））")
    public Result<String> assign(@RequestBody AssignToOneUserVo assignToOneUserVo) {
        resourceService.assignResourcePermission(assignToOneUserVo);
        return Result.success();
    }
}
