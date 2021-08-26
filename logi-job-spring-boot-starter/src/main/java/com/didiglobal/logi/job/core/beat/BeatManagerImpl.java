package com.didiglobal.logi.job.core.beat;

import com.didiglobal.logi.job.LogIJobProperties;
import com.didiglobal.logi.job.common.bean.AuvWorker;
import com.didiglobal.logi.job.common.domain.WorkerInfo;
import com.didiglobal.logi.job.core.WorkerSingleton;
import com.didiglobal.logi.job.core.job.JobManager;
import com.didiglobal.logi.job.mapper.AuvWorkerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeatManagerImpl implements BeatManager {
  private static final Logger logger = LoggerFactory.getLogger(BeatManagerImpl.class);

  private JobManager jobManager;
  private AuvWorkerMapper auvWorkerMapper;
  private LogIJobProperties logIJobProperties;

  private static final Long ONE_HOUR = 3600L;

  /**
   * constructor.
   *
   * @param jobManager job manager
   * @param auvWorkerMapper worker mapper
   */
  @Autowired
  public BeatManagerImpl(JobManager jobManager, AuvWorkerMapper auvWorkerMapper, LogIJobProperties logIJobProperties) {
    this.jobManager = jobManager;
    this.auvWorkerMapper = auvWorkerMapper;
    this.logIJobProperties = logIJobProperties;
  }

  @Override
  public boolean beat() {
    logger.info("class=BeatManagerImpl||method=||url=||msg=beat beat!!!");
    cleanWorker();

    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    workerSingleton.updateInstanceMetrics();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    workerInfo.setJobNum(jobManager.runningJobSize());
    workerInfo.setAppName(logIJobProperties.getAppName());
    return auvWorkerMapper.saveOrUpdateById(workerInfo.getWorker()) > 0 ? true : false;
  }

  @Override
  public boolean stop() {
    // clean worker
    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    auvWorkerMapper.deleteByCode(workerInfo.getCode());
    return true;
  }

  private void cleanWorker(){
    long currentTime = System.currentTimeMillis();
    List<AuvWorker> auvWorkers = auvWorkerMapper.selectByAppName(logIJobProperties.getAppName());
    for(AuvWorker auvWorker : auvWorkers){
      if(auvWorker.getUpdateTime().getTime() + 2 * 24 * ONE_HOUR * 1000  < currentTime){
        auvWorkerMapper.deleteByCode(auvWorker.getCode());
      }
    }
  }
}