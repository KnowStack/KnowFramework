package com.didiglobal.logi.auvjob.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 正在执行的job信息
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class JobInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * task code
   */
  private String code;

  /**
   * 任务code
   */
  private String taskCode;

  /**
   * 类的全限定名
   */
  private String className;

  /**
   * 第几次重试
   */
  private Integer retryTimes;

  /**
   * 开始时间
   */
  private LocalDateTime startTime;


}
