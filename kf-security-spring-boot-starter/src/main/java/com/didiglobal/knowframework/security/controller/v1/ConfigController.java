package com.didiglobal.knowframework.security.controller.v1;

import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.PagingResult;
import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.constant.Constants;
import com.didiglobal.knowframework.security.service.ConfigService;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.didiglobal.knowframework.security.util.HttpRequestUtil;
import com.didiglobal.knowframework.security.common.dto.config.ConfigDTO;
import com.didiglobal.knowframework.security.common.dto.config.ConfigQueryDTO;
import com.didiglobal.knowframework.security.common.vo.config.ConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cjm
 */
@RestController
@Api(value = "logi-security-config相关API接口", tags = "logi-security-配置相关API接口")
@RequestMapping(Constants.API_PREFIX_V1 + "/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("/list")
    @ResponseBody
    @ApiOperation(value = "获取配置列表接口", notes = "")
    public Result<List<ConfigVO>> list(@RequestBody ConfigDTO param) {
        return Result
                .buildSucc(CopyBeanUtil.copyList(configService.queryByCondt(param), ConfigVO.class));
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页配置列表", notes = "分页和条件查询")
    public PagingResult<ConfigVO> page(@RequestBody ConfigQueryDTO queryDTO) {
        PagingData<ConfigVO> configVO = configService.pagingConfig(queryDTO);
        return PagingResult.success(configVO);
    }

    @GetMapping("/group/list")
    @ResponseBody
    @ApiOperation(value = "获取配置的模块列表", notes = "")
    public Result<List<String>> groups() {
        return Result.buildSucc(configService.listGroups());
    }

    @GetMapping("/get")
    @ResponseBody
    @ApiOperation(value = "获取指定配置接口", notes = "")
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "配置ID", required = true) })
    public Result<ConfigVO> get(@RequestParam("configId") Integer configId) {
        return Result
                .buildSucc(CopyBeanUtil.copy(configService.getConfigById(configId), ConfigVO.class));
    }

    @PostMapping("/switch")
    @ResponseBody
    @ApiOperation(value = "配置开关接口", notes = "")
    public Result<Void> switchConfig(HttpServletRequest request, @RequestBody ConfigDTO param) {
        return configService.switchConfig(param.getId(), param.getStatus(),
                HttpRequestUtil.getOperator(request));
    }

    @DeleteMapping("/del")
    @ResponseBody
    @ApiOperation(value = "删除配置接口", notes = "")
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "配置ID", required = true) })
    public Result<Void> delete(HttpServletRequest request, @RequestParam(value = "id") Integer id) {
        return configService.delConfig(id, HttpRequestUtil.getOperator(request));
    }

    @PutMapping("/add")
    @ResponseBody
    @ApiOperation(value = "新建配置接口", notes = "")
    public Result<Integer> add(HttpServletRequest request, @RequestBody ConfigDTO param) {
        return configService.addConfig(param, HttpRequestUtil.getOperator(request));
    }

    @PostMapping("/edit")
    @ResponseBody
    @ApiOperation(value = "编辑配置接口", notes = "")
    public Result<Void> edit(HttpServletRequest request, @RequestBody ConfigDTO param) {
        return configService.editConfig(param, HttpRequestUtil.getOperator(request));
    }
}
