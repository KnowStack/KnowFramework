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

    @PostMapping("/mbu/list")
    @ApiOperation(value = "资源权限管理/按用户管理/分配资源/数据列表", notes = "获取数据（项目、类别、资源）list")
    public Result<List<MByUDataVo>> list(@RequestBody MByUDataQueryVo queryVo) {
        List<MByUDataVo> resultList = resourceService.getManagerByUserDataList(queryVo);
        return Result.success(resultList);
    }

    @PostMapping("/mbr/list")
    @ApiOperation(value = "资源权限管理/按资源管理/分配用户/数据列表", notes = "获取用户list")
    public Result<List<MByRDataVo>> list(@RequestBody MByRDataQueryVo queryVo) {
        List<MByRDataVo> resultList = resourceService.getManagerByResourceDataList(queryVo);
        return Result.success(resultList);
    }

    @PostMapping("/mbr/page")
    @ApiOperation(value = "资源权限管理/按资源管理/列表信息", notes = "按资源管理的列表信息，mbr（ManageByResource）")
    public PagingResult<MByRVo> page(@RequestBody MByRQueryVo queryVo) {
        PagingData<MByRVo> pagingData = resourceService.getManageByResourcePage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/mbu/page")
    @ApiOperation(value = "资源权限管理/按用户管理/列表信息", notes = "按用户管理的列表信息，mbu（ManageByUser）")
    public PagingResult<MByUVo> page(@RequestBody MByUQueryVo queryVo) {
        PagingData<MByUVo> pagingData = resourceService.getManageByUserPage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/mbr/assign")
    @ApiOperation(value = "资源权限管理/按资源管理/分配用户", notes = "1个项目或1个资源类别或1个具体资源的权限分配给N个用户")
    public Result<String> assign(@RequestBody AssignToManyUserVo assignToManyUserVo) {
        resourceService.assignResourcePermission(assignToManyUserVo);
        return Result.success();
    }

    @PostMapping("/mbu/assign")
    @ApiOperation(value = "资源权限管理/按用户管理/分配资源", notes = "N个项目或N个资源类别或N个具体资源的权限分配给1个用户")
    public Result<String> assign(@RequestBody AssignToOneUserVo assignToOneUserVo) {
        resourceService.assignResourcePermission(assignToOneUserVo);
        return Result.success();
    }

    @PostMapping("/assign/batch")
    @ApiOperation(
            value = "资源权限管理/批量分配用户和批量分配资源",
            notes = "批量分配用户：分配之前先删除N资源先前已分配的用户、批量分配资源：分配之前先删除N用户已拥有的资源权限"
    )
    public Result<String> assign(@RequestBody BatchAssignVo assignVo) {
        resourceService.batchAssignResourcePermission(assignVo);
        return Result.success();
    }

}
