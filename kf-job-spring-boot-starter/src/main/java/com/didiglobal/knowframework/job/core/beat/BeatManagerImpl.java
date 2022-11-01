package com.didiglobal.knowframework.job.core.beat;

import com.didiglobal.knowframework.job.common.domain.LogITask;
import com.didiglobal.knowframework.job.common.domain.LogIWorker;
import com.didiglobal.knowframework.job.core.job.JobManager;
import com.didiglobal.knowframework.job.LogIJobProperties;
import com.didiglobal.knowframework.job.common.po.LogITaskPO;
import com.didiglobal.knowframework.job.common.po.LogIWorkerPO;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.core.monitor.SimpleBeatMonitor;
import com.didiglobal.knowframework.job.mapper.LogITaskLockMapper;
import com.didiglobal.knowframework.job.mapper.LogITaskMapper;
import com.didiglobal.knowframework.job.mapper.LogIWorkerMapper;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;

@Service
public class BeatManagerImpl implements BeatManager {
    private static final Logger logger = LoggerFactory.getLogger(BeatManagerImpl.class);

    private JobManager jobManager;
    private LogIWorkerMapper    logIWorkerMapper;
    private LogITaskLockMapper  logITaskLockMapper;
    private LogITaskMapper      logITaskMapper;
    private LogIJobProperties   logIJobProperties;

    /**
     * constructor.
     *
     * @param jobManager       job manager
     * @param logIWorkerMapper worker mapper
     * @param logIJobProperties job 配置信息
     */
    @Autowired
    public BeatManagerImpl(JobManager jobManager,
                           LogIWorkerMapper logIWorkerMapper,
                           LogITaskLockMapper logITaskLockMapper,
                           LogITaskMapper      logITaskMapper,
                           LogIJobProperties logIJobProperties) {
        this.jobManager         = jobManager;
        this.logIWorkerMapper   = logIWorkerMapper;
        this.logITaskLockMapper = logITaskLockMapper;
        this.logITaskMapper     = logITaskMapper;
        this.logIJobProperties  = logIJobProperties;
    }

    @Override
    public boolean beat() {
        logger.info("class=BeatManagerImpl||method=beat||msg=beat beat!!!");
        cleanWorker();

        WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
        workerSingleton.updateInstanceMetrics();
        LogIWorker logIWorker = workerSingleton.getLogIWorker();
        logIWorker.setJobNum(jobManager.runningJobSize());
        logIWorker.setAppName(logIJobProperties.getAppName());

        int ret;
        if(null == logIWorkerMapper.selectByCode(logIWorker.getWorkerCode(), logIWorker.getAppName())){
            ret = logIWorkerMapper.insert(logIWorker.getWorker());
        }else {
            ret = logIWorkerMapper.updateByCode(logIWorker.getWorker());
        }

        return ret > 0 ? true : false;
    }

    @Override
    public boolean stop() {
        WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
        LogIWorker logIWorker = workerSingleton.getLogIWorker();
        logIWorkerMapper.deleteByCode(logIWorker.getWorkerCode());
        logITaskLockMapper.deleteByWorkerCodeAndAppName(logIWorker.getWorkerCode(), logIJobProperties.getAppName());
        return true;
    }

    /*********************************************** private method ***********************************************/
    private void cleanTask(String appName, String workCode){
        List<LogITaskPO>   logITaskPOS   = logITaskMapper.selectByAppName(appName);
        if(!CollectionUtils.isEmpty(logITaskPOS)){
            for(LogITaskPO logITaskPO : logITaskPOS){
                try {
                    List<LogITask.TaskWorker> taskWorkers = BeanUtil.convertToList(logITaskPO.getTaskWorkerStr(),
                            LogITask.TaskWorker.class);

                    if(CollectionUtils.isEmpty(taskWorkers)){continue;}

                    boolean needUpdate = false;

                    Iterator<LogITask.TaskWorker> iter = taskWorkers.iterator();
                    while (iter.hasNext()) {
                        LogITask.TaskWorker taskWorker = iter.next();

                        if(workCode.equals(taskWorker.getWorkerCode())){
                            iter.remove();
                            needUpdate = true;
                        }
                    }

                    if(needUpdate){
                        logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
                        logITaskMapper.updateTaskWorkStrByCode(logITaskPO);
                    }
                }catch (Exception e){
                    logger.info("class=BeatManagerImpl||method=cleanTask||msg=clean task worker error!", e);
                }
            }
        }
    }

    private void cleanWorker() {
        long currentTime = System.currentTimeMillis();
        String appName   = logIJobProperties.getAppName();

        List<LogIWorkerPO> logIWorkerPOS = logIWorkerMapper.selectByAppName(appName);
        if(CollectionUtils.isEmpty(logIWorkerPOS)){return;}

        for (LogIWorkerPO logIWorkerPO : logIWorkerPOS) {
            if (logIWorkerPO.getHeartbeat().getTime() + 3 * SimpleBeatMonitor.INTERVAL * 1000 < currentTime) {
                logIWorkerMapper.deleteByCode(logIWorkerPO.getWorkerCode());
                logITaskLockMapper.deleteByWorkerCodeAndAppName(logIWorkerPO.getWorkerCode(), appName);

                cleanTask(appName, logIWorkerPO.getWorkerCode());
            }
        }
    }
}