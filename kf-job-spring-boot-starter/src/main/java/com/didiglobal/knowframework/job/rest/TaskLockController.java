package com.didiglobal.knowframework.job.rest;

import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.vo.LogITaskLockVO;
import com.didiglobal.knowframework.job.core.task.TaskLockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.V1 + "/logi-job/taskLock")
@Api(tags = "logi-job 的任务锁相关接口")
public class TaskLockController {

    @Autowired
    private TaskLockService taskLockService;

    @PostMapping("/release")
    @ApiOperation(value = "释放某一个锁住的任务", notes = "")
    public Result<Boolean> release(@RequestParam String taskCode, @RequestParam String workerCode) {
        return Result.buildSucc(taskLockService.tryRelease(taskCode, workerCode));
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "获取所有锁住的任务", notes = "")
    public Result<List<LogITaskLockVO>> getAll() {
        return Result.buildSucc(taskLockService.getAll());
    }

}