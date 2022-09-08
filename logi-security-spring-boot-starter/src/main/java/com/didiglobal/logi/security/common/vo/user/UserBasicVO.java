package com.didiglobal.logi.security.common.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "只包含用户")
public class UserBasicVO {
	@ApiModelProperty(value = "用户 id", dataType = "Integer", required = false)
	private Integer id;
	
	@ApiModelProperty(value = "用户账号", dataType = "String", required = false)
	private String userName;
	
	@ApiModelProperty(value = "真实姓名", dataType = "String", required = false)
	private String  realName;
	@ApiModelProperty(value = "部门 id", dataType = "Integer", required = false)
	private Integer deptId;

}