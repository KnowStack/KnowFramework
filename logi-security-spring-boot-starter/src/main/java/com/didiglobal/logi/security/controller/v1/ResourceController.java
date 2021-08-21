package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.resource.*;
import com.didiglobal.logi.security.common.vo.resource.*;
import com.didiglobal.logi.security.service.ResourceTypeService;
import com.didiglobal.logi.security.service.UserResourceService;
import io.swagger.annotations.*;
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
    private UserResourceService userResourceService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    @GetMapping("/type/list")
    @ApiOperation(value = "获取所有资源类型", notes = "获取所有资源类型")
    public Result<List<ResourceTypeVO>> typeList() {
        List<ResourceTypeVO> resourceTypeVOList = resourceTypeService.getAllResourceTypeList();
        return Result.success(resourceTypeVOList);
    }

    @GetMapping("/vpc/status")
    @ApiOperation(value = "获取资源查看权限控制状态", notes = "true开启、false关闭，vpc（ViewPermissionControl）")
    public Result<Boolean> vpcStatus() {
        boolean isOn = userResourceService.getViewPermissionControlStatus();
        return Result.success(isOn);
    }

    @PutMapping("/vpc/switch")
    @ApiOperation(value = "更改资源查看权限控制状态", notes = "调用该接口则资源查看权限控制状态被反转")
    public Result<String> vpcSwitch() {
        userResourceService.changeResourceViewControlStatus();
        return Result.success();
    }

    @PostMapping("/mbu/list")
    @ApiOperation(value = "资源权限管理/按用户管理/分配资源/数据列表", notes = "获取数据（项目、类别、资源）list")
    public Result<List<MByUDataVO>> mbuList(@RequestBody MByUDataQueryDTO queryVo) {
        List<MByUDataVO> resultList = userResourceService.getManagerByUserDataList(queryVo);
        return Result.success(resultList);
    }

    @PostMapping("/mbr/list")
    @ApiOperation(value = "资源权限管理/按资源管理/分配用户/数据列表", notes = "获取用户list")
    public Result<List<MByRDataVO>> mbrList(@RequestBody MByRDataQueryDTO queryVo) {
        List<MByRDataVO> resultList = userResourceService.getManagerByResourceDataList(queryVo);
        return Result.success(resultList);
    }

    @PostMapping("/mbr/page")
    @ApiOperation(value = "资源权限管理/按资源管理/列表信息", notes = "按资源管理的列表信息，mbr（ManageByResource）")
    public PagingResult<MByRVO> mbrPage(@RequestBody MByRQueryDTO queryVo) {
        PagingData<MByRVO> pagingData = userResourceService.getManageByResourcePage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/mbu/page")
    @ApiOperation(value = "资源权限管理/按用户管理/列表信息", notes = "按用户管理的列表信息，mbu（ManageByUser）")
    public PagingResult<MByUVO> mbuPage(@RequestBody MByUQueryDTO queryVo) {
        PagingData<MByUVO> pagingData = userResourceService.getManageByUserPage(queryVo);
        return PagingResult.success(pagingData);
    }

    @PostMapping("/mbr/assign")
    @ApiOperation(value = "资源权限管理/按资源管理/分配用户", notes = "1个项目或1个资源类别或1个具体资源的权限分配给N个用户")
    public Result<String> mbrAssign(@RequestBody AssignToManyUserDTO assignToManyUserDTO) {
        userResourceService.assignResourcePermission(assignToManyUserDTO);
        return Result.success();
    }

    @PostMapping("/mbu/assign")
    @ApiOperation(value = "资源权限管理/按用户管理/分配资源", notes = "N个项目或N个资源类别或N个具体资源的权限分配给1个用户")
    public Result<String> mbuAssign(@RequestBody AssignToOneUserDTO assignToOneUserDTO) {
        userResourceService.assignResourcePermission(assignToOneUserDTO);
        return Result.success();
    }

    @PostMapping("/assign/batch")
    @ApiOperation(
            value = "资源权限管理/批量分配用户和批量分配资源",
            notes = "批量分配用户：分配之前先删除N资源先前已分配的用户、批量分配资源：分配之前先删除N用户已拥有的资源权限"
    )
    public Result<String> batchAssign(@RequestBody BatchAssignDTO assignVo) {
        userResourceService.batchAssignResourcePermission(assignVo);
        return Result.success();
    }
}
