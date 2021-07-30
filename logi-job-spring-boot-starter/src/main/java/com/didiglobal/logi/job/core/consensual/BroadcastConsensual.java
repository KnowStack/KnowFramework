package com.didiglobal.logi.job.core.consensual;

import com.didiglobal.logi.job.common.domain.TaskInfo;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author dengshan
 */
@Service
public class BroadcastConsensual extends AbstractConsensual {

  @Override
  public String getName() {
    return ConsensualConstant.BROADCAST.name();
  }

  @Override
  public boolean tryClaim(TaskInfo taskInfo) {
    return true;
  }
}
