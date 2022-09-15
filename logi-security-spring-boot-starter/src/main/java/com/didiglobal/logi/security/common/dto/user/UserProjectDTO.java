package com.didiglobal.logi.security.common.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserProjectDTO {
	@ApiModelProperty(value = "根据用户 id 查询", dataType = "Integer", required = false)
	private Integer id;
	/**
	 * 用户 id
	 */
	@ApiModelProperty(value = "用户 id", dataType = "Integer", required = false)
	private Integer userId;
	
	/**
	 * 用户类型：0：普通项目用户；1：项目 owner
	 */
	@ApiModelProperty(value = "用户类型", dataType = "Integer", required = false)
	private Integer userType;
	
	/**
	 * 项目 id
	 */
	@ApiModelProperty(value = "项目 id", dataType = "Integer", required = false)
	private Integer projectId;
	@ApiModelProperty(value = "是否被删除", dataType = "Boolean", required = false)
	private Boolean isDelete;
	
}