package com.didiglobal.logi.security.common.vo.record;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 * 对象类型
 */
@Data
@ApiModel(description = "对象类型信息")
public class TargetTypeVo {

    @ApiModelProperty(value = "对象类型标识", dataType = "Integer", required = false)
    private Integer type;

    @ApiModelProperty(value = "对象类型信息", dataType = "String", required = false)
    private String info;
}
