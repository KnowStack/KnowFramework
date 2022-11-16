package com.didiglobal.logi.job.core.worker;

import com.didiglobal.logi.job.LogIJobProperties;
import com.didiglobal.logi.job.common.domain.LogIWorker;
import com.didiglobal.logi.job.common.po.LogIWorkerPO;
import com.didiglobal.logi.job.mapper.LogIWorkerMapper;
import com.didiglobal.logi.job.utils.BeanUtil;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerManagerImpl implements WorkerManager {

    private static final ILog logger     = LogFactory.getLog(WorkerManagerImpl.class);

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
