package com.didiglobal.logi.security.common.dto.config;

import com.didiglobal.logi.security.common.dto.PageParamDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "配置查找条件信息")
public class ConfigQueryDTO extends PageParamDTO {
    @ApiModelProperty("配置名称")
    private String  valueName;

    @ApiModelProperty("备注/描述")
    private String  memo;

    @ApiModelProperty("状态(1 正常；2 禁用)")
    private Integer status;
}
