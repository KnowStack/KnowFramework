package com.didiglobal.logi.security.common.vo.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "按资源管理/分配资源/数据列表")
public class MByRDataVo {

    @ApiModelProperty(value = "用户id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户实名+用户账户名组合", dataType = "String", required = false)
    private String name;

    @ApiModelProperty(value = "拥有级别（0 不拥有、1 半拥有、2 全拥有）", dataType = "Integer", required = false)
    private Integer hasLevel;
}
