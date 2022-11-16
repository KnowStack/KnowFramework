package com.didiglobal.knowframework.job.core.consensual;

import com.didiglobal.knowframework.job.common.domain.LogITask;
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
    public boolean tryClaim(LogITask logITask) {
        return true;
    }
}
