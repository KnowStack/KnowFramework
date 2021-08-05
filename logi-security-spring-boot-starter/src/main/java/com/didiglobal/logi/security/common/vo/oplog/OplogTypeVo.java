package com.didiglobal.logi.security.common.vo.oplog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 * 操作类型
 */
@Data
@ApiModel(description = "操作类型信息")
public class OplogTypeVo {

    @ApiModelProperty(value = "操作类型标识", dataType = "Integer", required = false)
    private Integer type;

    @ApiModelProperty(value = "操作类型信息", dataType = "String", required = false)
    private String info;
}
