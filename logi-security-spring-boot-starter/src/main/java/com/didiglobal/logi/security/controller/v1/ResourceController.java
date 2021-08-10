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

    @GetMapping("/list/mbu")
    @ApiOperation(value = "资源权限管理/按用户管理/分配资源/数据列表", notes = "获取数据（项目、类别、资源）list")
    public Result<List<MByUDataVo>> list(@RequestParam MByUDataQueryVo queryVo) {
        List<MByUDataVo> resultList = resourceService.getManagerByUserDataList(queryVo);
        return Result.success(resultList);
    }

    @PostMapping("/page/mbr")
    @ApiOperation(value = "资源权限管理/按资源管理/列表信息", notes = "按资源管理的列表信息，接口中mbr就是ManageByResource")
    public PagingResult<MByRVo> page(@RequestBody MByRQueryVo queryVo) {
        PagingData<MByRVo> pagingData = resourceService.getManageByResourcePage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/page/mbu")
    @ApiOperation(value = "资源权限管理/按用户管理/列表信息", notes = "按用户管理的列表信息，接口中mbu就是ManageByUser")
    public PagingResult<MByUVo> page(@RequestBody MByUQueryVo queryVo) {
        PagingData<MByUVo> pagingData = resourceService.getManageByUserPage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/assign/mbr")
    @ApiOperation(value = "资源权限管理/按资源管理/分配用户", notes = "1个项目或1个资源类别或1个具体资源的权限分配给N个用户")
    public Result<String> assign(@RequestBody AssignToManyUserVo assignToManyUserVo) {
        resourceService.assignResourcePermission(assignToManyUserVo);
        return Result.success();
    }

    @PostMapping("/assign/mbu")
    @ApiOperation(value = "资源权限管理/按用户管理/分配资源", notes = "N个项目或N个资源类别或N个具体资源的权限分配给1个用户")
    public Result<String> assign(@RequestBody AssignToOneUserVo assignToOneUserVo) {
        resourceService.assignResourcePermission(assignToOneUserVo);
        return Result.success();
    }
}
