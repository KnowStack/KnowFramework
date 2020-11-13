package com.didiglobal.logi.auvjob.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务信息
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TaskInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * task code
   */
  private String code;

  /**
   * 名称
   */
  private String name;

  /**
   * 任务描述
   */
  private String desc;

  /**
   * cron 表达式
   */
  private String cron;

  /**
   * 类的全限定名
   */
  private String className;

  /**
   * 执行参数 map 形式{key1:value1,key2:value2}
   */
  private String params;

  /**
   * 允许重试次数
   */
  private Integer retry;

  /**
   * 上次执行时间
   */
  private Timestamp lastFireTime;

  /**
   * 下次执行时间
   */
  private Timestamp nextFireTime;

  /**
   * 超时 毫秒
   */
  private Long timeout;

  /**
   * 1运行中 2暂停
   */
  private Integer status;

  /**
   * 子任务code列表,逗号分隔
   */
  private String subtasks;


}
