package com.didiglobal.logi.security.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.record.RecordQueryVo;
import com.didiglobal.logi.security.common.vo.record.RecordTypeVo;
import com.didiglobal.logi.security.common.vo.record.RecordVo;
import com.didiglobal.logi.security.common.vo.record.TargetTypeVo;
import com.didiglobal.logi.security.service.RecordService;
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
@Api(value = "record相关API接口", tags = "操作日志相关API接口")
@RequestMapping("/v1/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @PostMapping("/page")
    @ApiOperation(value = "查询操作日志列表", notes = "分页和条件查询")
    public PagingResult<RecordVo> page(RecordQueryVo queryVo) {
        IPage<RecordVo> pageRecord = recordService.getPageRecord(queryVo);
        return PagingResult.success(pageRecord);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取操作日志详情", notes = "根据操作日志id获取操作日志详情")
    @ApiImplicitParam(name = "id", value = "操作日志id", dataType = "int", required = true)
    public Result<RecordVo> get(@PathVariable Integer id) {
        RecordVo recordVo = recordService.getDetailById(id);
        return Result.success(recordVo);
    }

    @GetMapping("/type/record")
    @ApiOperation(value = "获取操作记录类型", notes = "获取全部操作记录类型")
    public Result<List<RecordTypeVo>> recordType() {
        List<RecordTypeVo> recordTypeList = recordService.getRecordTypeList();
        return Result.success(recordTypeList);
    }

    @GetMapping("/type/target")
    @ApiOperation(value = "获取操作对象类型", notes = "获取全部操作对象类型")
    public Result<List<TargetTypeVo>> targetType() {
        List<TargetTypeVo> targetTypeList = recordService.getTargetTypeList();
        return Result.success(targetTypeList);
    }
}
