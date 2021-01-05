package com.didiglobal.logi.auvjob;

import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.core.SimpleScheduler;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import com.didiglobal.logi.auvjob.mapper.AuvWorkerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

@Service
public class ApplicationCloseListener implements ApplicationListener<ContextClosedEvent> {

  private static Logger logger = LoggerFactory.getLogger(ApplicationCloseListener.class);

  private AuvTaskLockMapper auvTaskLockMapper;
  private AuvWorkerMapper auvWorkerMapper;

  @Autowired
  public ApplicationCloseListener(AuvTaskLockMapper auvTaskLockMapper,
                                  AuvWorkerMapper auvWorkerMapper) {
    this.auvTaskLockMapper = auvTaskLockMapper;
    this.auvWorkerMapper = auvWorkerMapper;
  }

  @Override
  public void onApplicationEvent(ContextClosedEvent event) {
    // clean lock
    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    auvTaskLockMapper.deleteByWorkerCode(workerInfo.getCode());

    // clean worker
    auvWorkerMapper.deleteByCode(workerInfo.getCode());

    Object source = event.getSource();
    if (source != null && source instanceof SimpleScheduler) {
      ((SimpleScheduler) source).shutdown();
    }
  }
}