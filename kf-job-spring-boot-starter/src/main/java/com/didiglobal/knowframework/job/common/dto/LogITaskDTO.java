package com.didiglobal.knowframework.job.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "LogITask 任务信息")
public class LogITaskDTO {
    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "任务调度时间表达式")
    private String cron;

    @ApiModelProperty(value = "任务对应的类名")
    private String className;

    @ApiModelProperty(value = "任务执行参数")
    private String params;

    @ApiModelProperty(value = "任务重试次数")
    private Integer retryTimes;

    @ApiModelProperty(value = "任务抢占模式")
    private String consensual;
    /**
     * 执行节点白名单集
     */
    @ApiModelProperty(value = "执行节点白名单集")
    private String nodeNameWhiteListString;

}