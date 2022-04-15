package com.didiglobal.logi.security.common.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel(description = "用户信息")
public class UserDTO {

    @ApiModelProperty(value = "用户账号", dataType = "Integer", required = true)
    private String userName;

    @ApiModelProperty(value = "用户密码", dataType = "Integer", required = false)
    private String pw;

    @ApiModelProperty(value = "用户真实姓名", dataType = "Integer", required = false)
    private String realName;

    @ApiModelProperty(value = "用户角色id", dataType = "Integer", required = false)
    private List<Integer> roleIds;
}
