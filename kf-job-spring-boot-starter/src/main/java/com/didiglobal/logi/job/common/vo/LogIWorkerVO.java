package com.didiglobal.logi.job.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * worker 详情
 */
@Data
@ApiModel(description = "LogIWorkerVO 详情")
public class LogIWorkerVO {

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "appName")
    private String appName;

    @ApiModelProperty(value = "nodeName")
    private String nodeName;

}
