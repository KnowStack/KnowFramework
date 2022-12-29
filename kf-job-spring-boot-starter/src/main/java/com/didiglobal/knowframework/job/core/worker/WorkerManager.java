package com.didiglobal.knowframework.job.core.worker;

import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.domain.LogIWorker;
import com.didiglobal.knowframework.job.common.po.LogIWorkerPO;

import java.util.List;
import java.util.Map;

/**
 * worker 的CRUD及执行管控.
 */
public interface WorkerManager {

    /**
     * @return 返回系统当前所有 worker
     */
    List<LogIWorker> getAll();



    /**
     * 获取所有的 worker
     * @return
     */
    Result<List<String>> listAllWorkerIps();

    /**
     * 获取所有的 worker
     * @return
     */
    Map<String, LogIWorkerPO> mapAllWorkers();

}
