package com.didiglobal.logi.security.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.vo.project.ProjectQueryVo;
import com.didiglobal.logi.security.common.vo.project.ProjectSaveVo;
import com.didiglobal.logi.security.common.vo.project.ProjectVo;
import com.didiglobal.logi.security.service.ProjectService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "project相关API接口", tags = "项目相关API接口")
@RequestMapping("/v1/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{id}")
    @ApiOperation(value = "获取项目详情", notes = "根据项目id获取项目详情")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<ProjectVo> detail(@PathVariable Integer id) {
        ProjectVo projectVo = projectService.getDetailById(id);
        return Result.success(projectVo);
    }

    @PutMapping("/switch/{id}")
    @ApiOperation(value = "更改项目运行状态", notes = "调用该接口则项目运行状态被反转")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<String> switched(@PathVariable Integer id) {
        projectService.changeProjectStatus(id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation(value = "更新项目信息", notes = "根据项目id更新项目信息")
    public Result<String> update(@RequestBody ProjectSaveVo projectSaveVo) {
        projectService.updateProjectBy(projectSaveVo);
        return Result.success();
    }

    @PostMapping
    @ApiOperation(value = "创建项目", notes = "创建项目")
    public Result<String> create(@RequestBody ProjectSaveVo projectSaveVo) {
        projectService.createProject(projectSaveVo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除项目", notes = "根据项目id删除项目")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<String> delete(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation(value = "查询项目列表", notes = "分页和条件查询")
    public PagingResult<ProjectVo> page(@RequestBody ProjectQueryVo queryVo) {
        PagingData<ProjectVo> pageProject = projectService.getProjectPage(queryVo);
        return PagingResult.success(pageProject);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取所有项目概况信息", notes = "获取全部项目（只返回id、项目名）")
    public Result<List<ProjectVo>> list() {
        List<ProjectVo> projectVo = projectService.getProjectList();
        return Result.success(projectVo);
    }
}
