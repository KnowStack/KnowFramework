package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogVo;
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
    public PagingResult<OplogVo> page(@RequestBody OplogQueryVo queryVo) {
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

    @GetMapping("/extra/{type}")
    @ApiOperation(value = "获取操作日志列表的查询条件信息", notes = "查询条件中操作类型、对象类型的下拉信息")
    @ApiImplicitParam(name = "type", value = "哪种信息：1：操作页面、2：操作类型、3：对象分类", required = true)
    public Result<List<String>> extra(@PathVariable Integer type) {
        List<String> oplogExtraList = oplogService.getOplogExtraList(type);
        return Result.success(oplogExtraList);
    }
}
