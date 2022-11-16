package com.didiglobal.knowframework.job.common.dto;

import lombok.Data;

@Data
public class LogITaskDTO {

    private String name;
    private String description;
    private String cron;
    private String className;
    private String params;
    private Integer retryTimes;
    private String consensual;
    /**
     * 执行节点白名单集
     */
    private String nodeNameWhiteListString;

}