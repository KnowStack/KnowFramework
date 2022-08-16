package com.didiglobal.logi.job.core.worker;

import com.didiglobal.logi.job.LogIJobProperties;
import com.didiglobal.logi.job.common.domain.LogIWorker;
import com.didiglobal.logi.job.common.po.LogIWorkerPO;
import com.didiglobal.logi.job.mapper.LogIWorkerMapper;
import com.didiglobal.logi.job.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerManagerImpl implements WorkerManager {

    private static final Logger logger = LoggerFactory.getLogger(WorkerManagerImpl.class);

    private LogIWorkerMapper logIWorkerMapper;
    private LogIJobProperties logIJobProperties;

    /**
     * constructor.
     * @param logIWorkerMapper  logIWorkerMapper
     * @param logIJobProperties 配置信息
     */
    public WorkerManagerImpl(LogIWorkerMapper logIWorkerMapper,
                           LogIJobProperties logIJobProperties) {
        this.logIWorkerMapper = logIWorkerMapper;
        this.logIJobProperties = logIJobProperties;
    }

    @Override
    public List<LogIWorker> getAll() {
        List<LogIWorkerPO> logIWorkerPOList = logIWorkerMapper.selectByAppName(logIJobProperties.getAppName());
        List<LogIWorker> logIWorkerList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(logIWorkerPOList)) {
            for (LogIWorkerPO logIWorkerPO : logIWorkerPOList) {
                logIWorkerList.add(BeanUtil.convertTo(logIWorkerPO, LogIWorker.class));
            }
        }
        return logIWorkerList;
    }

}
