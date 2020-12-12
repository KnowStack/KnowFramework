package com.didiglobal.logi.auvjob.core;

import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author dengshan
 */
@Service
public class RandomConsensual implements Consensual {

  private NodeManager nodeManager;

  @Override
  public boolean canClaim(TaskInfo taskInfo) {

    return true;
  }
}
