package com.didiglobal.logi.auvjob.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * worker信息
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WorkerInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * job log code
   */
  private String code;

  /**
   * worker名
   */
  private String name;

  /**
   * cpu
   */
  private String cpu;

  /**
   * cpu使用
   */
  private String cpuUsed;

  /**
   * 内存
   */
  private String memory;

  /**
   * 内存使用
   */
  private String memoryUsed;

  /**
   * jvm堆大小
   */
  private String jvmHeap;

  /**
   * jvm堆使用
   */
  private String jvmHeapUsed;

  /**
   * 正在执行job数
   */
  private Integer jobNum;

  /**
   * 心跳时间
   */
  private LocalDateTime heartbeat;


}
