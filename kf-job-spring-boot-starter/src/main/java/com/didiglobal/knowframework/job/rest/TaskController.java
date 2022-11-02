package com.didiglobal.knowframework.job.rest;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.dto.KfTaskPageQueryDTO;
import com.didiglobal.knowframework.job.core.task.TaskManager;
import com.didiglobal.knowframework.job.common.PagingResult;
import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.vo.KfLogITaskVO;
import com.didiglobal.knowframework.job.core.consensual.ConsensualEnum;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.didiglobal.knowframework.job.common.CommonUtil.sqlFuzzyQueryTransfer;

/**
 * task controller.
 *
 * @author ds
 */
@RestController
@RequestMapping(Constants.V1 + "/logi-job/task")
@Api(tags = "kf-job 配置的 task 相关接口")
public class TaskController {

    @Autowired
    private TaskManager taskManager;

    @PostMapping("/{taskCode}/do")
    public Result<Boolean> execute(@PathVariable String taskCode) {
        return taskManager.execute(taskCode, false);
    }

    @PostMapping("/list")
    public PagingResult<KfLogITaskVO> getAll(@RequestBody KfTaskPageQueryDTO kfTaskPageQueryDTO) {
        kfTaskPageQueryDTO.setTaskDesc(sqlFuzzyQueryTransfer( kfTaskPageQueryDTO.getTaskDesc()));
        kfTaskPageQueryDTO.setClassName(sqlFuzzyQueryTransfer( kfTaskPageQueryDTO.getClassName()));

        List<KfTask> kfTasks = taskManager.getPagineList( kfTaskPageQueryDTO );
        int count = taskManager.pagineTaskConut( kfTaskPageQueryDTO );

        return PagingResult.buildSucc(
                logITask2LogITaskVO( kfTasks ), count, kfTaskPageQueryDTO.getPage(), kfTaskPageQueryDTO.getSize()
        );
    }

    @PostMapping("/{taskCode}/{status}")
    public Result<Boolean> status(@PathVariable String taskCode, @PathVariable Integer status) {
        return taskManager.updateTaskStatus(taskCode, status);
    }

    @GetMapping("/{taskCode}/detail")
    public Result<KfLogITaskVO> detail(@PathVariable String taskCode) {
        return Result.buildSucc(logITask2LogITaskVO(taskManager.getByCode(taskCode)));
    }

    @PostMapping("/{taskCode}/{workerCode}/release")
    public Result<Boolean> release(@PathVariable String taskCode, @PathVariable String workerCode) {
        return taskManager.release(taskCode, workerCode);
    }

    @DeleteMapping("/{taskCode}")
    public Result<Boolean> delete(@PathVariable String taskCode) {
        return taskManager.delete(taskCode);
    }

    /**************************************** private method ****************************************************/
    private List<KfLogITaskVO> logITask2LogITaskVO(List<KfTask> kfTasks) {
        if (CollectionUtils.isEmpty( kfTasks )) {
            return new ArrayList<>();
        }

        return kfTasks.stream().map( l -> logITask2LogITaskVO(l)).collect(Collectors.toList());
    }

    private KfLogITaskVO logITask2LogITaskVO(KfTask kfTask) {
        KfLogITaskVO kfLogITaskVO = BeanUtil.convertTo( kfTask, KfLogITaskVO.class);

        if (!CollectionUtils.isEmpty( kfTask.getTaskWorkers())) {
            List<String> ips = kfTask.getTaskWorkers().stream().map( w -> w.getIp()).collect(Collectors.toList());

            kfLogITaskVO.setRouting(ConsensualEnum.getByName( kfTask.getConsensual()).getDesc());
            kfLogITaskVO.setWorkerIps(ips);
        }

        return kfLogITaskVO;
    }
}
