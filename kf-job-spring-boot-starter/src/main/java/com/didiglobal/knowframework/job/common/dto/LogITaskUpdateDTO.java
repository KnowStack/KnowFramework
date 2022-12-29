package com.didiglobal.knowframework.job.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author didi
 */
@Data
@ApiModel(description = "LogITask 任务编辑DTO")
public class LogITaskUpdateDTO {

    @ApiModelProperty(value = "调度执行器的ip列表")
    private List<String> workerIps;

    @ApiModelProperty(value = "调度执行器的参数")
    private String param;
}