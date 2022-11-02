package com.didiglobal.knowframework.job.common.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class KfTaskLockDTO {
    private Long id;
    private String taskCode;
    private String workerCode;
    private Timestamp createTime;
    private Timestamp updateTime;
}