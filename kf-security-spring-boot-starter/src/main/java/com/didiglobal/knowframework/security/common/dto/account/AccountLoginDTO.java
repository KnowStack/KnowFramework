package com.didiglobal.knowframework.security.common.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author cjm
 */
@Data
@ApiModel(description = "用户登陆信息")
public class AccountLoginDTO {

    @ApiModelProperty(name = "userName", value = "用户登录名（可以是用户名登录或者邮箱登录）", dataType = "String")
    private String userName;

    @ApiModelProperty(name = "pw", value = "用户登录密码", dataType = "String")
    private String pw;
}
