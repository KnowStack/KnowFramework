package com.didiglobal.logi.job.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 任务详情
 *
 * @author cjm
 */
@Data
@ApiModel(description = "LogITaskVO 任务详情")
public class LogITaskVO {

    @ApiModelProperty(value = "taskcode")
    private String code;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "任务责任人")
    private String owner;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "定时任务调度时间表达式")
    private String cron;

    @ApiModelProperty(value = "定时任务调度执行代码")
    private String className;

    @ApiModelProperty(value = "执行参数 map 形式{key1:value1,key2:value2}")
    private String params;

    @ApiModelProperty(value = "上次调度执行时间")
    private Timestamp lastFireTime;

    @ApiModelProperty(value = "下次调度执行时间")
    private Timestamp nextFireTime;

    @ApiModelProperty(value = "任务状态，1：正常，0：暂停")
    private Integer status;

    @ApiModelProperty(value = "调度方式：单播、广播")
    private String consensual;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "调度机器列表")
    private List<String> workerIps;
}
