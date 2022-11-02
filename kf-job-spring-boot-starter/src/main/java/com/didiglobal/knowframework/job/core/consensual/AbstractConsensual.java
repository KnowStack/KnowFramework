package com.didiglobal.knowframework.job.core.consensual;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.domain.KfWorker;
import com.didiglobal.knowframework.job.common.po.KfWorkerBlacklistPO;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.mapper.KfWorkerBlacklistMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author ds
 */
@Service
public abstract class AbstractConsensual implements Consensual {

    private static final Logger logger = LoggerFactory.getLogger(AbstractConsensual.class);

    @Autowired
    private KfWorkerBlacklistMapper kfWorkerBlacklistMapper;

    private static final String BLACKLIST_KEY = "BlacklistKey";

    private Cache<Object, Set<String>> blacklistCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES).build();

    @Override
    public boolean canClaim(KfTask kfTask) {
        if (inBlacklist()) {
            return false;
        }
        return tryClaim( kfTask );
    }

    public abstract boolean tryClaim(KfTask kfTask);

    //###################################### private ################################################

    private boolean inBlacklist() {
        Set<String> blacklist = blacklist();
        KfWorker kfWorker = WorkerSingleton.getInstance().getKfWorker();
        return blacklist.contains( kfWorker.getWorkerCode());
    }

    private Set<String> blacklist() {
        Set<String> blacklist = new HashSet<>();
        try {
            blacklist = blacklistCache.get(BLACKLIST_KEY, () -> {
                List<KfWorkerBlacklistPO> kfWorkerBlacklistPOS = kfWorkerBlacklistMapper.selectAll();
                return kfWorkerBlacklistPOS.stream().map( KfWorkerBlacklistPO::getWorkerCode)
                        .collect(Collectors.toSet());
            });
        } catch (ExecutionException e) {
            logger.error("class=AbstractConsensual||method=blacklist||url=||msg=", e);
        }
        return blacklist;
    }
}
