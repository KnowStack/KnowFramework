package com.didiglobal.knowframework.job.core.beat;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.domain.KfWorker;
import com.didiglobal.knowframework.job.core.job.JobManager;
import com.didiglobal.knowframework.job.KfJobProperties;
import com.didiglobal.knowframework.job.common.po.KfTaskPO;
import com.didiglobal.knowframework.job.common.po.KfWorkerPO;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.core.monitor.SimpleBeatMonitor;
import com.didiglobal.knowframework.job.mapper.KfTaskLockMapper;
import com.didiglobal.knowframework.job.mapper.KfTaskMapper;
import com.didiglobal.knowframework.job.mapper.KfWorkerMapper;
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
    private KfWorkerMapper kfWorkerMapper;
    private KfTaskLockMapper kfTaskLockMapper;
    private KfTaskMapper kfTaskMapper;
    private KfJobProperties kfJobProperties;

    /**
     * constructor.
     *
     * @param jobManager       job manager
     * @param kfWorkerMapper worker mapper
     * @param kfJobProperties job 配置信息
     */
    @Autowired
    public BeatManagerImpl(JobManager jobManager,
                           KfWorkerMapper kfWorkerMapper,
                           KfTaskLockMapper kfTaskLockMapper,
                           KfTaskMapper kfTaskMapper,
                           KfJobProperties kfJobProperties) {
        this.jobManager         = jobManager;
        this.kfWorkerMapper = kfWorkerMapper;
        this.kfTaskLockMapper = kfTaskLockMapper;
        this.kfTaskMapper = kfTaskMapper;
        this.kfJobProperties = kfJobProperties;
    }

    @Override
    public boolean beat() {
        logger.info("class=BeatManagerImpl||method=beat||msg=beat beat!!!");
        cleanWorker();

        WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
        workerSingleton.updateInstanceMetrics();
        KfWorker kfWorker = workerSingleton.getKfWorker();
        kfWorker.setJobNum(jobManager.runningJobSize());
        kfWorker.setAppName( kfJobProperties.getAppName());

        int ret;
        if(null == kfWorkerMapper.selectByCode( kfWorker.getWorkerCode(), kfWorker.getAppName())){
            ret = kfWorkerMapper.insert( kfWorker.getWorker());
        }else {
            ret = kfWorkerMapper.updateByCode( kfWorker.getWorker());
        }

        return ret > 0 ? true : false;
    }

    @Override
    public boolean stop() {
        WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
        KfWorker kfWorker = workerSingleton.getKfWorker();
        kfWorkerMapper.deleteByCode( kfWorker.getWorkerCode());
        kfTaskLockMapper.deleteByWorkerCodeAndAppName( kfWorker.getWorkerCode(), kfJobProperties.getAppName());
        return true;
    }

    /*********************************************** private method ***********************************************/
    private void cleanTask(String appName, String workCode){
        List<KfTaskPO> kfTaskPOS = kfTaskMapper.selectByAppName(appName);
        if(!CollectionUtils.isEmpty( kfTaskPOS )){
            for(KfTaskPO kfTaskPO : kfTaskPOS){
                try {
                    List<KfTask.TaskWorker> taskWorkers = BeanUtil.convertToList( kfTaskPO.getTaskWorkerStr(),
                            KfTask.TaskWorker.class);

                    if(CollectionUtils.isEmpty(taskWorkers)){continue;}

                    boolean needUpdate = false;

                    Iterator<KfTask.TaskWorker> iter = taskWorkers.iterator();
                    while (iter.hasNext()) {
                        KfTask.TaskWorker taskWorker = iter.next();

                        if(workCode.equals(taskWorker.getWorkerCode())){
                            iter.remove();
                            needUpdate = true;
                        }
                    }

                    if(needUpdate){
                        kfTaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
                        kfTaskMapper.updateTaskWorkStrByCode( kfTaskPO );
                    }
                }catch (Exception e){
                    logger.info("class=BeatManagerImpl||method=cleanTask||msg=clean task worker error!", e);
                }
            }
        }
    }

    private void cleanWorker() {
        long currentTime = System.currentTimeMillis();
        String appName   = kfJobProperties.getAppName();

        List<KfWorkerPO> kfWorkerPOS = kfWorkerMapper.selectByAppName(appName);
        if(CollectionUtils.isEmpty( kfWorkerPOS )){return;}

        for (KfWorkerPO kfWorkerPO : kfWorkerPOS) {
            if (kfWorkerPO.getHeartbeat().getTime() + 3 * SimpleBeatMonitor.INTERVAL * 1000 < currentTime) {
                kfWorkerMapper.deleteByCode( kfWorkerPO.getWorkerCode());
                kfTaskLockMapper.deleteByWorkerCodeAndAppName( kfWorkerPO.getWorkerCode(), appName);

                cleanTask(appName, kfWorkerPO.getWorkerCode());
            }
        }
    }
}