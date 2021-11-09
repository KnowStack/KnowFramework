package com.didiglobal.logi.job.core.beat;

import com.didiglobal.logi.job.LogIJobProperties;
import com.didiglobal.logi.job.common.domain.LogITask;
import com.didiglobal.logi.job.common.domain.LogIWorker;
import com.didiglobal.logi.job.common.po.LogITaskLockPO;
import com.didiglobal.logi.job.common.po.LogITaskPO;
import com.didiglobal.logi.job.common.po.LogIWorkerPO;
import com.didiglobal.logi.job.core.WorkerSingleton;
import com.didiglobal.logi.job.core.job.JobManager;
import com.didiglobal.logi.job.core.monitor.SimpleBeatMonitor;
import com.didiglobal.logi.job.mapper.LogITaskLockMapper;
import com.didiglobal.logi.job.mapper.LogITaskMapper;
import com.didiglobal.logi.job.mapper.LogIWorkerMapper;
import com.didiglobal.logi.job.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BeatManagerImpl implements BeatManager {
    private static final Logger logger = LoggerFactory.getLogger(BeatManagerImpl.class);

    private JobManager          jobManager;
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
        clean();

        WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
        workerSingleton.updateInstanceMetrics();
        LogIWorker logIWorker = workerSingleton.getLogIWorker();
        logIWorker.setJobNum(jobManager.runningJobSize());
        logIWorker.setAppName(logIJobProperties.getAppName());
        return logIWorkerMapper.saveOrUpdateById(logIWorker.getWorker()) > 0 ? true : false;
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
    private void clean(){
        String appName   = logIJobProperties.getAppName();

        Map<String, LogIWorkerPO> logIWorkerPOMap = cleanWorker(appName);
        cleanTask(appName, logIWorkerPOMap);
        cleanLock(appName, logIWorkerPOMap);
    }

    private void cleanTask(String appName, Map<String, LogIWorkerPO> logIWorkerPOMap){
        List<LogITaskPO>   logITaskPOS   = logITaskMapper.selectByAppName(appName);
        if(!CollectionUtils.isEmpty(logITaskPOS)){
            for(LogITaskPO logITaskPO : logITaskPOS){
                try {
                    List<LogITask.TaskWorker> taskWorkers = BeanUtil.convertToList(logITaskPO.getTaskWorkerStr(),
                            LogITask.TaskWorker.class);

                    if(CollectionUtils.isEmpty(taskWorkers)){continue;}

                    Iterator<LogITask.TaskWorker> iter = taskWorkers.iterator();
                    while (iter.hasNext()) {
                        LogITask.TaskWorker taskWorker = iter.next();

                        if(null == logIWorkerPOMap
                                || null == logIWorkerPOMap.get(taskWorker.getWorkerCode())){
                            iter.remove();
                        }
                    }

                    logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
                    logITaskMapper.updateTaskWorkStrByCode(logITaskPO);
                }catch (Exception e){
                    logger.info("class=BeatManagerImpl||method=cleanTask||msg=clean task worker error!", e);
                }
            }
        }
    }

    private void cleanLock(String appName, Map<String, LogIWorkerPO> logIWorkerPOMap){
        List<LogITaskLockPO> logITaskLockPOS = logITaskLockMapper.selectByAppName(appName);
        if(!CollectionUtils.isEmpty(logITaskLockPOS)){
            Iterator<LogITaskLockPO> iter = logITaskLockPOS.iterator();

            while (iter.hasNext()) {
                LogITaskLockPO logITaskLockPO = iter.next();

                try {
                    if(null == logIWorkerPOMap
                            || null == logIWorkerPOMap.get(logITaskLockPO.getWorkerCode())){
                        iter.remove();
                        logITaskLockMapper.deleteByWorkerCodeAndAppName(logITaskLockPO.getWorkerCode(), appName);
                    }
                }catch (Exception e){
                    logger.info("class=BeatManagerImpl||method=cleanLock||msg=clean task worker error!", e);
                }
            }
        }
    }

    private Map<String, LogIWorkerPO> cleanWorker(String appName) {
        long currentTime = System.currentTimeMillis();

        List<LogIWorkerPO> logIWorkerPOS = logIWorkerMapper.selectByAppName(logIJobProperties.getAppName());
        if(CollectionUtils.isEmpty(logIWorkerPOS)){return null;}

        for (LogIWorkerPO logIWorkerPO : logIWorkerPOS) {
            if (logIWorkerPO.getHeartbeat().getTime() + 3 * SimpleBeatMonitor.INTERVAL * 1000 < currentTime) {
                logIWorkerMapper.deleteByCode(logIWorkerPO.getWorkerCode());
            }
        }

        List<LogIWorkerPO> logIWorkerPOS1 = logIWorkerMapper.selectByAppName(appName);
        if(CollectionUtils.isEmpty(logIWorkerPOS1)){return null;}

        return logIWorkerPOS1.stream().collect(Collectors.toMap(LogIWorkerPO::getWorkerCode, (p) -> p));
    }
}