package com.didiglobal.logi.job.rest;

import com.didiglobal.logi.job.common.PagingResult;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.common.dto.TaskLogPageQueryDTO;
import com.didiglobal.logi.job.common.vo.LogIJobLogVO;
import com.didiglobal.logi.job.core.job.JobLogManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.didiglobal.logi.job.common.Result;

/**
 * job 的启动、暂停、job信息、job日志相关操作.
 *
 * @author ds
 */
@RestController
@RequestMapping(Constants.V1 + "/logi-job/logs")
@Api(tags = "logi-job 执行生成的任务日志相关接口")
public class JobLogsController {
    @Autowired
    private JobLogManager jobLogManager;

    @PostMapping("/list")
    public PagingResult<LogIJobLogVO> getJobLogs(@RequestBody TaskLogPageQueryDTO pageQueryDTO) {
        List<LogIJobLogVO> logIJobLogVOS = jobLogManager.pageJobLogs(pageQueryDTO);
        int totalCount = jobLogManager.getJobLogsCount(pageQueryDTO);

        return PagingResult.buildSucc(logIJobLogVOS, totalCount, pageQueryDTO.getPage(), pageQueryDTO.getSize());
    }

    @ApiOperation(value = "查看执行生成的任务日志", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<String>> getLogById(@PathVariable Long id) {
        TaskResult taskResult = jobLogManager.getJobLogResult(id);
        if(null == taskResult) {
            return Result.buildFail(
                    String.format(
                            "无法获取执行生成的任务日志：系统中不存在id为%d的JobLog执行结果",
                            id
                    )
            );
        } else {
            String traceId = taskResult.getTraceId();
            if(StringUtils.isNotBlank(traceId)) {
                List<String> logs = jobLogManager.getLogsByTraceIdFromExternalSystem(traceId);
                return Result.buildSucc(logs);
            } else {
                return Result.buildFail(
                        String.format(
                                "无法获取执行生成的任务日志：id为%d的JobLog执行结果中不包含对应 traceId",
                                id
                        )
                );
            }
        }
    }

}
