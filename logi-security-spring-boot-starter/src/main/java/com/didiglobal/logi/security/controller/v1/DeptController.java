package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.dept.DeptTreeVO;
import com.didiglobal.logi.security.service.DeptService;
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
@Api(value = "dept相关API接口", tags = "部门相关API接口")
@RequestMapping("/v1/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/tree")
    @ApiOperation(value = "获取所有部门", notes = "以树的形式返回所有部门")
    public Result<DeptTreeVO> tree() {
        DeptTreeVO deptTreeVO = deptService.buildDeptTree();
        return Result.success(deptTreeVO);
    }
}
