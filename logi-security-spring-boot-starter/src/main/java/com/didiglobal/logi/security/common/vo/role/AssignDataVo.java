package com.didiglobal.logi.security.common.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "分配角色或者分配用户/列表信息")
public class AssignDataVo {

    @ApiModelProperty(value = "分配用户/用户id，分配角色/角色id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "分配用户/用户名，分配角色/角色名", dataType = "Integer", required = false)
    private String name;

    @ApiModelProperty(value = "是有拥有", dataType = "Integer", required = false)
    private boolean isHas;
}
