package com.didiglobal.knowframework.job.common.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class KfJobDTO {

    private String code;
    private String taskCode;
    private String className;
    private Integer tryTimes;
    private String workerCode;
    private Timestamp startTime;
    private Timestamp createTime;
    private Timestamp updateTime;

}