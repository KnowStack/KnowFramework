package com.didiglobal.knowframework.job.rest;

import com.didiglobal.knowframework.job.utils.BeanUtil;
import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.domain.LogIWorker;
import com.didiglobal.knowframework.job.common.vo.LogIWorkerVO;
import com.didiglobal.knowframework.job.core.worker.WorkerManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * worker controller.
 */
@RestController
@RequestMapping(Constants.V1 + "/logi-job/worker")
@Api(tags = "logi-job 配置的 worker 相关接口")
public class WorkerController {

    @Autowired
    private WorkerManager workerManager;

    @GetMapping("/list")
    public Result<List<LogIWorkerVO>> getAll() {
        List<LogIWorker> logIWorkerList = workerManager.getAll();
        List<LogIWorkerVO> logIWorkerVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(logIWorkerList)) {
            for (LogIWorker logIWorker : logIWorkerList) {
                logIWorkerVOList.add(BeanUtil.convertTo(logIWorker, LogIWorkerVO.class));
            }
        }
        return Result.buildSucc(logIWorkerVOList);
    }

}
