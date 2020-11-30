package com.didiglobal.logi.auvjob.common.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TaskInfo {

  private String code;
  private String name;
  private String desc;
  private String cron;
  private String className;
  /*
   * 执行参数 map 形式{key1:value1,key2:value2}
   */
  private String params;
  private Integer retryTimes;
  private LocalDateTime lastFireTime;
  private LocalDateTime nextFireTime;
  private Long timeout;
  private Integer status;
  private String subTaskCodes;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

}