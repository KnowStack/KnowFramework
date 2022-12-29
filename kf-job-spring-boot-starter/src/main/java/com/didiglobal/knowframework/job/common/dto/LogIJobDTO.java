package com.didiglobal.knowframework.job.common.dto;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "LogITask 作业信息")
public class LogIJobDTO {

    @ApiModelProperty(value = "作业code")
    private String code;

    @ApiModelProperty(value = "作业所属任务code")
    private String taskCode;

    @ApiModelProperty(value = "作业所属任务的类信息")
    private String className;

    @ApiModelProperty(value = "作业重试次数")
    private Integer tryTimes;

    @ApiModelProperty(value = "调度器地址")
    private String workerCode;

    @ApiModelProperty(value = "作业开始执行时间")
    private Timestamp startTime;

    @ApiModelProperty(value = "作业创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "作业更新时间")
    private Timestamp updateTime;

}