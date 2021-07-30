package com.didiglobal.logi.security.common.vo.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "项目添加或更新信息")
public class ProjectSaveVo {

    @ApiModelProperty(value = "项目id（更新操作必备）", dataType = "Integer", required = false)
    private Integer id;

    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名", dataType = "String", required = true)
    private String projectName;

    /**
     * 项目负责人
     */
    @ApiModelProperty(value = "项目负责人", dataType = "List<Integer>", required = true)
    private List<Integer> chargeUserIdList;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", dataType = "String", required = false)
    private String description;

    /**
     * 运行状态（true启动 or false停用）
     */
    @ApiModelProperty(value = "运行状态（true启动 or false停用）", dataType = "Boolean", required = false)
    private Boolean isRunning = true;

    /**
     * 使用部门id
     */
    @ApiModelProperty(value = "使用部门id", dataType = "Integer", required = true)
    private Integer deptId;
}
