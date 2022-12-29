package com.didiglobal.knowframework.job.common.dto;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "LogITask 作业日志信息")
public class LogIJobLogDTO {

    @ApiModelProperty(value = "作业名称")
    private String jobCode;

    @ApiModelProperty(value = "作业所属任务名称")
    private String taskCode;

    @ApiModelProperty(value = "作业所属任务的类名")
    private String className;

    @ApiModelProperty(value = "作业失败重试次数")
    private Integer tryTimes;

    @ApiModelProperty(value = "作业调度器地址")
    private String workerCode;

    @ApiModelProperty(value = "作业开始执行时间")
    private Timestamp startTime;

    @ApiModelProperty(value = "作业执行结束时间")
    private Timestamp endTime;

    @ApiModelProperty(value = "作业状态")
    private Integer status;

    @ApiModelProperty(value = "作业执行失败信息")
    private String error;

    @ApiModelProperty(value = "作业创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "作业更新时间")
    private Timestamp updateTime;

    @ApiModelProperty(value = "作业执行结果")
    private String result;

    @ApiModelProperty(value = "作业执行人")
    private String operator;
}