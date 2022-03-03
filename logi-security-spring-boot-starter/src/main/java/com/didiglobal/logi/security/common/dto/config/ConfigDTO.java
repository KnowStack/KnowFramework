package com.didiglobal.logi.security.common.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "配置信息")
public class ConfigDTO {
    @ApiModelProperty("配置ID")
    private Integer id;

    @ApiModelProperty("配置组")
    private String  valueGroup;

    @ApiModelProperty("配置名称")
    private String  valueName;

    @ApiModelProperty("值")
    private String  value;

    @ApiModelProperty("状态(1 正常；2 禁用；-1 删除)")
    private Integer status;

    @ApiModelProperty("备注")
    private String  memo;

    @ApiModelProperty("操纵者")
    private String  operator;
}
