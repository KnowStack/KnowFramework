package com.didiglobal.knowframework.job.core.worker;

import com.didiglobal.knowframework.job.utils.BeanUtil;
import com.didiglobal.knowframework.job.LogIJobProperties;
import com.didiglobal.knowframework.job.common.domain.LogIWorker;
import com.didiglobal.knowframework.job.common.po.LogIWorkerPO;
import com.didiglobal.knowframework.job.mapper.LogIWorkerMapper;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
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
