package com.didiglobal.logi.auvjob.common.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WorkerInfo {

  private String code;
  private String name;

  private Integer cpu;

  private Double cpuUsed;

  /*
   * 以M为单位
   */
  private Double memory;

  private Double memoryUsed;

  /*
   * 以M为单位
   */
  private Double jvmMemory;

  private Double jvmMemoryUsed;

  private Integer jobNum;

  private LocalDateTime heartbeat;

}