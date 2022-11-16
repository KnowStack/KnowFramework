package com.didiglobal.knowframework.job.core.worker;

import com.didiglobal.knowframework.job.common.domain.LogIWorker;

import java.util.List;

/**
 * worker 的CRUD及执行管控.
 */
public interface WorkerManager {

    /**
     * @return 返回系统当前所有 worker
     */
    List<LogIWorker> getAll();

}
