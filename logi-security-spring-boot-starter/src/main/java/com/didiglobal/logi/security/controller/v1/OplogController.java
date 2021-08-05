package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogTypeVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogVo;
import com.didiglobal.logi.security.common.vo.oplog.TargetTypeVo;
import com.didiglobal.logi.security.service.OplogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "oplog相关API接口", tags = "操作日志相关API接口")
@RequestMapping("/v1/oplog")
public class OplogController {

    @Autowired
    private OplogService oplogService;

    @PostMapping("/page")
    @ApiOperation(value = "查询操作日志列表", notes = "分页和条件查询")
    public PagingResult<OplogVo> page(OplogQueryVo queryVo) {
        PagingData<OplogVo> pageOplog = oplogService.getOplogPage(queryVo);
        return PagingResult.success(pageOplog);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取操作日志详情", notes = "根据操作日志id获取操作日志详情")
    @ApiImplicitParam(name = "id", value = "操作日志id", dataType = "int", required = true)
    public Result<OplogVo> get(@PathVariable Integer id) {
        OplogVo oplogVo = oplogService.getDetailById(id);
        return Result.success(oplogVo);
    }

    @GetMapping("/type/oplog")
    @ApiOperation(value = "获取操作记录类型", notes = "获取全部操作记录类型")
    public Result<List<OplogTypeVo>> operateType() {
        List<OplogTypeVo> operateTypeList = oplogService.getOperateTypeList();
        return Result.success(operateTypeList);
    }

    @GetMapping("/type/target")
    @ApiOperation(value = "获取操作对象类型", notes = "获取全部操作对象类型")
    public Result<List<TargetTypeVo>> targetType() {
        List<TargetTypeVo> targetTypeList = oplogService.getTargetTypeList();
        return Result.success(targetTypeList);
    }
}
