package com.didiglobal.knowframework.job.common.vo;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogITaskLockVO {
    @ApiModelProperty(value = "任务锁id")
    private Long id;

    @ApiModelProperty(value = "任务code")
    private String taskCode;

    @ApiModelProperty(value = "调度器")
    private String workerCode;

    @ApiModelProperty(value = "任务锁创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "任务锁更新时间")
    private Timestamp updateTime;
}