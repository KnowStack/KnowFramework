package com.didiglobal.logi.security.common.dto.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "角色添加或更新信息")
public class RoleSaveDTO {

    @ApiModelProperty(value = "角色id（更新操作必备）", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "角色名", dataType = "String", required = true)
    private String roleName;

    @ApiModelProperty(value = "角色描述", dataType = "String", required = true)
    private String description;
    
    @ApiModelProperty(value = "角色拥有的权限idList（角色权限不可为空）", dataType = "String", required = true)
    private List<Integer> permissionIdList;
}
