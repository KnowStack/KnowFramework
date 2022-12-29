package com.didiglobal.knowframework.job.rest;

import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.core.job.JobManager;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * job 的启动、暂停、job信息、job日志相关操作.
 *
 * @author ds
 */
@RestController
@RequestMapping(Constants.V1 + "/logi-job/job")
@Api(tags = "logi-job 执行生成的作业相关接口")
public class JobController {
    private static final ILog logger     = LogFactory.getLog(JobController.class);

    @Autowired
    private JobManager jobManager;

    @PostMapping("/{jobCode}/stop")
    @ApiOperation(value = "停止一个作业的执行", notes = "")
    public Result<Boolean> stop(@PathVariable String jobCode) {
        return Result.buildSucc(jobManager.stopByJobCode(jobCode));
    }

    @GetMapping("/runningJobs")
    @ApiOperation(value = "获取所有在执行的作业", notes = "")
    public Result getRunningJobs() {
        return Result.buildSucc(jobManager.getJobs());
    }
}
