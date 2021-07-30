package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.entity.Dept;
import com.didiglobal.logi.security.common.vo.dept.DeptVo;
import com.didiglobal.logi.security.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "dept相关API接口", tags = "部门相关API接口")
@RequestMapping("/v1/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/list")
    @ApiOperation(value = "获取所有部门", notes = "")
    public Result<List<DeptVo>> list() {
        List<DeptVo> deptList = deptService.getAllDept();
        return Result.success(deptList);
    }
}
