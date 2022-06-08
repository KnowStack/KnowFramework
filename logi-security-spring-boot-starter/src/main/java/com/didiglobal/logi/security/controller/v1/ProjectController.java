package com.didiglobal.logi.security.controller.v1;

import com.didiglobal.logi.security.common.constant.Constants;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.PagingResult;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.project.ProjectQueryDTO;
import com.didiglobal.logi.security.common.dto.project.ProjectSaveDTO;
import com.didiglobal.logi.security.common.vo.project.ProjectBriefVO;
import com.didiglobal.logi.security.common.vo.project.ProjectDeleteCheckVO;
import com.didiglobal.logi.security.common.vo.project.ProjectVO;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.service.ProjectService;
import com.didiglobal.logi.security.util.HttpRequestUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "logi-security-project相关API接口", tags = "logi-security-项目相关API接口")
@RequestMapping(Constants.API_PREFIX_V1 + "/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{id}")
    @ApiOperation(value = "获取项目详情", notes = "根据项目id获取项目详情")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<ProjectVO> detail(@PathVariable Integer id) {
        try {
            ProjectVO projectVO = projectService.getProjectDetailByProjectId(id);
            return Result.success(projectVO);
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
    }

    @GetMapping("/{id}/exist")
    @ApiOperation(value = "校验项目是否存在", notes = "校验项目是否存在")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<Boolean> checkExist(@PathVariable Integer id) {
        return Result.buildSucc(projectService.checkProjectExist(id));
    }

    @PutMapping("/switch/{id}")
    @ApiOperation(value = "更改项目运行状态", notes = "调用该接口则项目运行状态被反转")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<String> switched(@PathVariable Integer id, HttpServletRequest request) {
        projectService.changeProjectStatus(id, HttpRequestUtil.getOperator(request));
        return Result.success();
    }

    @PutMapping
    @ApiOperation(value = "更新项目", notes = "根据项目id更新项目信息")
    public Result<String> update(@RequestBody ProjectSaveDTO saveDTO, HttpServletRequest request) {
        try {
            projectService.updateProject(saveDTO, HttpRequestUtil.getOperator(request));
        } catch (LogiSecurityException e) {
            e.printStackTrace();
            return Result.fail(e);
        }
        return Result.success();
    }

    @PostMapping
    @ApiOperation(value = "创建项目", notes = "创建项目")
    public Result<ProjectVO> create(@RequestBody ProjectSaveDTO saveDTO, HttpServletRequest request) {
        try {
            ProjectVO projectVO = projectService.createProject(saveDTO, HttpRequestUtil.getOperator(request));
            return Result.success(projectVO);
        } catch (LogiSecurityException e) {
            e.printStackTrace();
            return Result.fail(e);
        }
    }

    @GetMapping("/delete/check/{id}")
    @ApiOperation(value = "删除项目前的检查", notes = "检查是否有服务引用了该项目、是否有具体资源挂上了该项目")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<ProjectDeleteCheckVO> deleteCheck(@PathVariable Integer id) {
        ProjectDeleteCheckVO deleteCheckVO = projectService.checkBeforeDelete(id);
        return Result.success(deleteCheckVO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除项目", notes = "根据项目id删除项目")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<String> delete(@PathVariable Integer id, HttpServletRequest request) {
        projectService.deleteProjectByProjectId(id, HttpRequestUtil.getOperator(request));
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页查询项目列表", notes = "分页和条件查询")
    public PagingResult<ProjectVO> page(@RequestBody ProjectQueryDTO queryDTO) {
        PagingData<ProjectVO> pageProject = projectService.getProjectPage(queryDTO);
        return PagingResult.success(pageProject);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取所有项目简要信息", notes = "获取全部项目简要信息（只返回id、项目名）")
    public Result<List<ProjectBriefVO>> list() {
        List<ProjectBriefVO> projectBriefVOList = projectService.getProjectBriefList();
        return Result.success(projectBriefVOList);
    }

    @PutMapping("/{id}/owner/{ownerId}")
    @ApiOperation(value = "从角色中增加该项目下的负责人", notes = "从角色中增加该项目下的用户")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<String> addProjectOwner(@PathVariable Integer id, @PathVariable Integer ownerId, HttpServletRequest request) {
        try {
            projectService.addProjectOwner(id, ownerId, HttpRequestUtil.getOperator(request));
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}/owner/{ownerId}")
    @ApiOperation(value = "从项目中删除该项目下的负责人", notes = "从项目中删除该项目下的负责人")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<String> deleteProjectOwner(@PathVariable Integer id, @PathVariable Integer ownerId, HttpServletRequest request) {
        try {
            projectService.delProjectOwner(id, ownerId, HttpRequestUtil.getOperator(request));
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
        return Result.success();
    }

    @PutMapping("/{id}/user/{userId}")
    @ApiOperation(value = "从角色中增加该项目下的用户", notes = "从角色中增加该项目下的用户")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<String> addProjectUser(@PathVariable Integer id, @PathVariable Integer userId, HttpServletRequest request) {
        try {
            projectService.addProjectUser(id, userId, HttpRequestUtil.getOperator(request));
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}/user/{userId}")
    @ApiOperation(value = "从项目中删除该项目下的用户", notes = "从项目中删除该项目下的用户")
    @ApiImplicitParam(name = "id", value = "角色id", dataType = "int", required = true)
    public Result<String> deleteProjectUser(@PathVariable Integer id, @PathVariable Integer userId, HttpServletRequest request) {
        try {
            projectService.delProjectUser(id, userId, HttpRequestUtil.getOperator(request));
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
        return Result.success();
    }
    
    @GetMapping("/unassigned")
    @ApiOperation(value = "获取项目未分配的用户列表", notes = "获取项目未分配的用户列表")
    @ApiImplicitParam(name = "id", value = "项目id", dataType = "int", required = true)
    public Result<List<UserBriefVO>> unassigned(@RequestParam("id") Integer id) {
        try {
            return projectService.unassignedByProjectId(id);
        } catch (LogiSecurityException e) {
            return Result.fail(e);
        }
    }
    @GetMapping("/user/{userId}")
    @ApiOperation(value = "获取用户绑定的项目列表", notes = "获取用户绑定的项目列表")
    @ApiImplicitParam(name = "userId", value = "项目id", dataType = "int", required = true)
    public Result<List<ProjectBriefVO>> getProjectBriefByUserId(@PathVariable("userId") Integer userId) {
        return projectService.getProjectBriefByUserId(userId);
    }
    
}