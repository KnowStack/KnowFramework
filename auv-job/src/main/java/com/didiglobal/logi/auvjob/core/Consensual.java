package com.didiglobal.logi.auvjob.core;

import com.didiglobal.logi.auvjob.common.bean.TaskInfo;

/**
 * 任务执行策略，实现为全局而不是任务的主要考虑：每个任务配置成本高
 * @author dengshan
 */
public interface Consensual {

  /**
   * 节点能否认领任务
   * @Param taskInfo todo 判断有任务信息应该比较合适
   * @return
   */
  boolean canClaim(TaskInfo taskInfo);
}
