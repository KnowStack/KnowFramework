package com.didiglobal.logi.auvjob.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * job执行历史日志.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AuvJobLog implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /*
   * job code
   */
  private String jobCode;

  /*
   * 任务code
   */
  private String taskCode;

  /*
   * 类的全限定名
   */
  private String className;

  /*
   * 第几次重试
   */
  private Integer tryTimes;

  /*
   * 开始时间
   */
  private LocalDateTime startTime;

  /*
   * 结束时间
   */
  private LocalDateTime endTime;

  /*
   * 执行结果 1成功 2失败 3取消
   */
  private Integer status;

  /*
   * 错误信息
   */
  private String error;

  /*
   * 开始时间
   */
  private LocalDateTime createTime;

  /*
   * 开始时间
   */
  private LocalDateTime updateTime;

  /*
   * 执行结果
   */
  private String result;

}
