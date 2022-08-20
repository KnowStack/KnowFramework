package com.didiglobal.logi.job.rest;

import com.didiglobal.logi.job.common.Result;
import com.didiglobal.logi.job.core.job.JobManager;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * job 的启动、暂停、job信息、job日志相关操作.
 *
 * @author ds
 */
@RestController
@RequestMapping(Constants.V1 + "/logi-job/job")
@Api(tags = "logi-job 执行生成的 job 相关接口")
public class JobController {
    private static final ILog logger     = LogFactory.getLog(JobController.class);

    @Autowired
    private JobManager jobManager;

    @PostMapping("/{jobCode}/stop")
    public Result<Boolean> stop(@PathVariable String jobCode) {
        return Result.buildSucc(jobManager.stopByJobCode(jobCode));
    }

    @GetMapping("/runningJobs")
    public Result getRunningJobs() {
        return Result.buildSucc(jobManager.getJobs());
    }
}
