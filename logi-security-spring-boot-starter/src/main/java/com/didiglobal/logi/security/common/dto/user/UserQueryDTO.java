package com.didiglobal.logi.security.common.dto.user;

import com.didiglobal.logi.security.common.dto.PageParamDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 * 用户列表查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "用户查找条件信息")
public class UserQueryDTO extends PageParamDTO {

    @ApiModelProperty(value = "根据角色id查询", dataType = "Integer", required = false)
    private Integer roleId;

    @ApiModelProperty(value = "用户账号", dataType = "String", required = false)
    private String username;

    @ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
    private String realName;

    @ApiModelProperty(value = "根据部门id查询", dataType = "Integer", required = false)
    private Integer deptId;
}
