package com.didiglobal.logi.auvjob;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

@Service
public class AuvJobStopListener implements ApplicationListener<ContextClosedEvent> {

  @Autowired
  private AuvTaskLockMapper auvTaskLockMapper;

  @Override
  public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
    // 1.关闭本机持有的锁
    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    auvTaskLockMapper.delete(new QueryWrapper<AuvTaskLock>().eq("worker_code",
            workerInfo.getCode()));

  }
}