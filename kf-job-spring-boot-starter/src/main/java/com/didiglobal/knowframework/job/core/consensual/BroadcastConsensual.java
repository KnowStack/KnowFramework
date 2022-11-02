package com.didiglobal.knowframework.job.core.consensual;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author ds
 */
@Service
public class BroadcastConsensual extends AbstractConsensual {

    @Override
    public String getName() {
        return ConsensualEnum.BROADCAST.name();
    }

    @Override
    public boolean tryClaim(KfTask kfTask) {
        return true;
    }
}
