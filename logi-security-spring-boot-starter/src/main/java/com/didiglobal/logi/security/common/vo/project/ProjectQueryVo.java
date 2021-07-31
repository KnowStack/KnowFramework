package com.didiglobal.logi.security.common.vo.project;

import com.didiglobal.logi.security.common.vo.PageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "项目查找条件信息")
public class ProjectQueryVo extends PageParamVo {

    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名", dataType = "String", required = false)
    private String projectName;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号", dataType = "String", required = false)
    private String projectCode;

    /**
     * 负责人的用户账号名
     */
    @ApiModelProperty(value = "负责人的账号名", dataType = "String", required = false)
    private String chargeUsername;

    /**
     * 所属部门id
     */
    @ApiModelProperty(value = "所属部门id", dataType = "Integer", required = false)
    private Integer deptId;

    /**
     * 如果isRunning为null，则表示所有状态
     */
    @ApiModelProperty(value = "项目运行状态", dataType = "Boolean", required = false)
    private Boolean isRunning;
}
