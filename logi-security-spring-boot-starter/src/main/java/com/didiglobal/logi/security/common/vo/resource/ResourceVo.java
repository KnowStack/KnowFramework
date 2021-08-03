package com.didiglobal.logi.security.common.vo.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cjm
 *
 * 具体资源信息
 */
@Data
@ApiModel(description = "具体资源信息")
public class ResourceVo {

    @ApiModelProperty(value = "资源id", dataType = "Integer", required = false)
    private Integer resourceId;

    @ApiModelProperty(value = "资源名", dataType = "String", required = false)
    private String resourceName;

    @ApiModelProperty(value = "项目id", dataType = "Integer", required = true)
    private Integer projectId;

    @ApiModelProperty(value = "资源类别id", dataType = "Integer", required = true)
    private Integer resourceTypeId;
}
