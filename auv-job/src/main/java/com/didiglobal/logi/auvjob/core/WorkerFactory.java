package com.didiglobal.logi.auvjob.core;

import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;

/**
 * worker创建工厂.
 *
 * @author dengshan
 */
public interface WorkerFactory {

  Scheduler instantiate();

  WorkerInfo newWorker();
}
