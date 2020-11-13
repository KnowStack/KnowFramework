package com.didiglobal.logi.auvjob.core;

/**
 * worker创建工厂
 * @author dengshan
 */
public interface WorkerFactory {

  Worker instantiate();

  Worker getWorker();
}
