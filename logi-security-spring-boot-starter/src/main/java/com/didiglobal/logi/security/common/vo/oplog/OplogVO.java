package com.didiglobal.logi.security.common.vo.oplog;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author cjm
 */
@Data
@ApiModel(description = "操作日志信息")
public class OplogVO {

    @ApiModelProperty(value = "操作日志id", dataType = "Integer", required = false)
    private Integer id;

    @ApiModelProperty(value = "操作者ip", dataType = "String", required = false)
    private String operatorIp;

    @ApiModelProperty(value = "操作者用户账号", dataType = "String", required = false)
    private String operator;

    @ApiModelProperty(value = "操作页面", dataType = "String", required = false)
    private String operatePage;

    @ApiModelProperty(value = "操作类型", dataType = "String", required = false)
    private String operateType;

    @ApiModelProperty(value = "操作对象", dataType = "String", required = false)
    private String target;

    @ApiModelProperty(value = "对象分类", dataType = "String", required = false)
    private String targetType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "操作日志详情", dataType = "String", required = false)
    private String detail;

    @ApiModelProperty(value = "记录时间（时间戳ms）", dataType = "Long", required = false)
    private Date createTime;

    @ApiModelProperty(value = "更新时间（时间戳ms）", dataType = "Long", required = false)
    private Date updateTime;
}
