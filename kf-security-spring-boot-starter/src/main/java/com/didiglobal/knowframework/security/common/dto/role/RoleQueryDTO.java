package com.didiglobal.knowframework.security.common.dto.role;

import com.didiglobal.knowframework.security.common.dto.PageParamDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "角色查找条件信息")
public class RoleQueryDTO extends PageParamDTO {

    @ApiModelProperty(value = "角色编号（精确）", dataType = "String", required = false)
    private String roleCode;
    @ApiModelProperty(value = "id（精确）", dataType = "String", required = false)
    private Integer id;

    @ApiModelProperty(value = "角色名（模糊）", dataType = "String", required = false)
    private String roleName;

    @ApiModelProperty(value = "描述（模糊）", dataType = "String", required = false)
    private String description;
}