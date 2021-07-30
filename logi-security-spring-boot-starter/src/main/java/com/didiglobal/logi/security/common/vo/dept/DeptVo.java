package com.didiglobal.logi.security.common.vo.dept;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "部门信息")
public class DeptVo {

    @ApiModelProperty(value = "项目名", dataType = "Integer", required = false)
    private Integer id;

    /**
     * 部门名
     */
    @ApiModelProperty(value = "部门名", dataType = "String", required = false)
    private String deptName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", dataType = "String", required = false)
    private String description;
}
