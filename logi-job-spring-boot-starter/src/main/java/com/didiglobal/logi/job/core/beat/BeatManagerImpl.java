package com.didiglobal.logi.job.core.beat;

import com.didiglobal.logi.job.AuvJobProperties;
import com.didiglobal.logi.job.common.domain.WorkerInfo;
import com.didiglobal.logi.job.core.WorkerSingleton;
import com.didiglobal.logi.job.core.job.JobManager;
import com.didiglobal.logi.job.mapper.AuvWorkerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeatManagerImpl implements BeatManager {
  private static final Logger logger = LoggerFactory.getLogger(BeatManagerImpl.class);

  private JobManager jobManager;
  private AuvWorkerMapper auvWorkerMapper;
  private AuvJobProperties auvJobProperties;

  /**
   * constructor.
   *
   * @param jobManager job manager
   * @param auvWorkerMapper worker mapper
   */
  @Autowired
  public BeatManagerImpl(JobManager jobManager, AuvWorkerMapper auvWorkerMapper, AuvJobProperties auvJobProperties) {
    this.jobManager = jobManager;
    this.auvWorkerMapper = auvWorkerMapper;
    this.auvJobProperties = auvJobProperties;
  }

  @Override
  public boolean beat() {
    logger.info("class=BeatManagerImpl||method=||url=||msg=beat beat!!!");
    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    workerSingleton.updateInstanceMetrics();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    workerInfo.setJobNum(jobManager.runningJobSize());
    workerInfo.setAppName(auvJobProperties.getAppName());
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

}