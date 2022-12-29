package com.didiglobal.knowframework.job.core.task;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.job.common.po.LogIWorkerPO;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.core.consensual.Consensual;
import com.didiglobal.knowframework.job.core.consensual.ConsensualEnum;
import com.didiglobal.knowframework.job.core.consensual.ConsensualFactory;
import com.didiglobal.knowframework.job.core.job.JobManager;
import com.didiglobal.knowframework.job.core.monitor.SimpleTaskMonitor;
import com.didiglobal.knowframework.job.core.worker.WorkerManager;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import com.didiglobal.knowframework.job.LogIJobProperties;
import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.domain.LogITask;
import com.didiglobal.knowframework.job.common.dto.TaskPageQueryDTO;
import com.didiglobal.knowframework.job.common.enums.TaskStatusEnum;
import com.didiglobal.knowframework.job.common.po.LogITaskPO;
import com.didiglobal.knowframework.job.common.dto.LogITaskDTO;
import com.didiglobal.knowframework.job.common.enums.TaskWorkerStatusEnum;
import com.didiglobal.knowframework.job.mapper.LogITaskMapper;
import com.didiglobal.knowframework.job.utils.CronExpression;
import com.didiglobal.knowframework.job.utils.IdWorker;
import com.didiglobal.knowframework.job.utils.ThreadUtil;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static com.didiglobal.knowframework.job.common.CommonUtil.isCopyTask;

/**
 * task manager impl.
 *
 * @author ds
 */
@Service
public class TaskManagerImpl implements TaskManager {
    private static final ILog logger     = LogFactory.getLog(TaskManagerImpl.class);

    private static final long WAIT_INTERVAL_SECONDS = 10L;

    private static final Long TASK_TIME_OUT_DEFAULT_VALUE = 0L;
    private static final ConsensualEnum TASK_CONSENSUAL_DEFAULT_VALUE = ConsensualEnum.RANDOM;
    private static final String OWNER_DEFAULT_VALUE = "system";
    private static final Integer RETRY_TIMES_DEFAULT_VALUE = 0;

    private WorkerManager workerManager;
    private JobManager jobManager;
    private ConsensualFactory consensualFactory;
    private TaskLockService taskLockService;
    private LogITaskMapper logITaskMapper;
    private LogIJobProperties logIJobProperties;

    /**
     * constructor.
     *
     * @param jobManager      jobManager
     * @param taskLockService taskLockService
     * @param logITaskMapper  logITaskMapper
     * @param logIJobProperties 配置信息
     * @param consensualFactory 一致协议工厂
     */
    public TaskManagerImpl(WorkerManager workerManager, JobManager jobManager, ConsensualFactory consensualFactory,
                           TaskLockService taskLockService, LogITaskMapper logITaskMapper,
                           LogIJobProperties logIJobProperties) {
        this.workerManager      = workerManager;
        this.jobManager = jobManager;
        this.consensualFactory = consensualFactory;
        this.taskLockService = taskLockService;
        this.logITaskMapper = logITaskMapper;
        this.logIJobProperties = logIJobProperties;
    }

    @Override
    public Result delete(String taskCode) {
        LogITaskPO logITaskPO = logITaskMapper.selectByCode(taskCode, logIJobProperties.getAppName());
        if (logITaskPO == null) {
            return Result.buildFail("任务不存在！");
        }
        return Result.buildSucc(logITaskMapper.deleteByCode(taskCode, logIJobProperties.getAppName()) > 0);
    }

    @Override
    public boolean update(LogITaskDTO logITaskDTO) {
        LogITaskPO logITaskPO = BeanUtil.convertTo(logITaskDTO, LogITaskPO.class);
        return logITaskMapper.updateByCode(logITaskPO) > 0 ? true : false;
    }

    @Override
    public List<LogITask> nextTriggers(Long interval) {
        return nextTriggers(System.currentTimeMillis(), interval);
    }

    @Override
    public List<LogITask> nextTriggers(Long fromTime, Long interval) {
        List<LogITask> logITaskList = getAllRuning();

        logITaskList = logITaskList.stream().filter(taskInfo -> {
            try {
                if(ConsensualEnum.RANDOM.name().equals(taskInfo.getConsensual())){
                    Timestamp lastFireTime = taskInfo.getLastFireTime();

                    List<LogITask.TaskWorker> taskWorkers = taskInfo.getTaskWorkers();
                    for (LogITask.TaskWorker taskWorker : taskWorkers) {
                        // 取到当前worker做进一步判断，如果没有找到证明没有执行过
                        if (Objects.equals(WorkerSingleton.getInstance().getLogIWorker().getWorkerCode(),
                                taskWorker.getWorkerCode())) {
                            // 判断是否在当前worker可执行状态
                            if (!Objects.equals(taskWorker.getStatus(), TaskWorkerStatusEnum.WAITING.getValue())) {
                                logger.debug("class=TaskManagerImpl||method=nextTriggers||msg=has task running! "
                                                + "taskCode={}, workerCode={}", taskInfo.getTaskCode(),
                                        taskWorker.getWorkerCode());
                                return false;
                            }
                            break;
                        }
                    }

                    CronExpression cronExpression = new CronExpression(taskInfo.getCron());
                    long nextTime = cronExpression.getNextValidTimeAfter(lastFireTime).getTime();
                    taskInfo.setNextFireTime(new Timestamp(nextTime));

                    Timestamp timestamp = new Timestamp(fromTime + interval * 1000);
                    return timestamp.after(taskInfo.getNextFireTime());
                }else if(ConsensualEnum.BROADCAST.name().equals(taskInfo.getConsensual())){
                    List<LogITask.TaskWorker> taskWorkers = taskInfo.getTaskWorkers();
                    Timestamp lastFireTime = new Timestamp(0L);

                    for (LogITask.TaskWorker taskWorker : taskWorkers) {
                        // 取到当前worker做进一步判断，如果没有找到证明没有执行过
                        if (Objects.equals(WorkerSingleton.getInstance().getLogIWorker().getWorkerCode(),
                                taskWorker.getWorkerCode())) {

                            lastFireTime = taskWorker.getLastFireTime();
                        }
                    }

                    CronExpression cronExpression = new CronExpression(taskInfo.getCron());
                    long nextTime = cronExpression.getNextValidTimeAfter(lastFireTime).getTime();
                    taskInfo.setNextFireTime(new Timestamp(nextTime));

                    Timestamp timestamp = new Timestamp(fromTime + interval * 1000);

                    if(timestamp.after(new Timestamp(nextTime))){
                        if((nextTime + SimpleTaskMonitor.SCAN_INTERVAL_SLEEP_SECONDS * 1000) < fromTime
                                && fromTime < (nextTime + 2 * SimpleTaskMonitor.SCAN_INTERVAL_SLEEP_SECONDS * 1000)){
                            logger.info("class=TaskManagerImpl||method=nextTriggers||nextTime={}||fromTime={}||msg=skip broadcast duplicate trigger!",
                                    nextTime, fromTime);

                            for (LogITask.TaskWorker taskWorker : taskWorkers) {
                                if (Objects.equals(WorkerSingleton.getInstance().getLogIWorker().getWorkerCode(),
                                        taskWorker.getWorkerCode())) {

                                    taskWorker.setLastFireTime(new Timestamp(nextTime));

                                    LogITaskPO logITaskPO = BeanUtil.convertTo(taskInfo, LogITaskPO.class);
                                    logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));

                                    logITaskMapper.updateTaskWorkStrByCode(logITaskPO);
                                    return false;
                                }
                            }
                        }

                        return true;
                    }

                    return false;
                }

                return false;
            } catch (Exception e) {
                logger.error("class=TaskManagerImpl||method=nextTriggers||msg=exception!", e);
                return false;
            }
        }).collect(Collectors.toList());

        // sort
        logITaskList.sort(Comparator.comparing(LogITask::getNextFireTime));
        return logITaskList;
    }

    @Override
    public void submit(List<LogITask> logITaskList) {
        if (CollectionUtils.isEmpty(logITaskList)) {
            return;
        }
        for (LogITask logITask : logITaskList) {
            // 不能在本工作器执行，跳过
            Consensual consensual = consensualFactory.getConsensual(logITask.getConsensual());
            if (!consensual.canClaim(logITask, logIJobProperties)) {
                continue;
            }
            execute(logITask, false);
        }
    }

    /**
     * execute.
     */
    @Override
    public Result execute(String taskCode, Boolean executeSubs) {
        LogITaskPO logITaskPO = logITaskMapper.selectByCode(taskCode, logIJobProperties.getAppName());
        if (logITaskPO == null) {
            return Result.buildFail("任务不存在！");
        }
        if (!taskLockService.tryAcquire(taskCode)) {
            return Result.buildFail("未能获取到执行锁！");
        }

        LogITask logITask = logITaskPO2LogITask(logITaskPO);
        logITask.setTaskCallback(code -> taskLockService.tryRelease(code));
        execute(logITask, false);

        return Result.buildSucc();
    }

    @Override
    public void execute(LogITask logITask, Boolean executeSubs) {
        Timestamp lastFireTime = new Timestamp(System.currentTimeMillis());

        LogITaskPO logITaskPO = BeanUtil.convertTo(logITask, LogITaskPO.class);
        List<LogITask.TaskWorker> taskWorkers = logITask.getTaskWorkers();
        boolean copyTask    = isCopyTask(logITask.getTaskCode());
        boolean worked      = false;

        for (LogITask.TaskWorker taskWorker : taskWorkers) {
            if (Objects.equals(taskWorker.getWorkerCode(),
                    WorkerSingleton.getInstance().getLogIWorker().getWorkerCode())) {
                taskWorker.setLastFireTime(lastFireTime);
                taskWorker.setStatus(TaskWorkerStatusEnum.RUNNING.getValue());
                worked = true;
                break;
            }
        }

        if (!copyTask && !worked) {
            taskWorkers.add(new LogITask.TaskWorker(TaskWorkerStatusEnum.RUNNING.getValue(),
                    new Timestamp(System.currentTimeMillis()),
                    WorkerSingleton.getInstance().getLogIWorker().getWorkerCode(),
                    WorkerSingleton.getInstance().getLogIWorker().getIp()));
        }

        logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
        logITaskPO.setLastFireTime(lastFireTime);
        // 更新任务状态，最近更新时间
        logITaskMapper.updateByCode(logITaskPO);

        // 执行
        executeInternal(logITask, executeSubs);
    }

    @Override
    public int stopAll() {
        return jobManager.stopAll();
    }

    @Override
    public Result<Boolean> updateTaskStatus(String taskCode, int status) {
        if (!TaskStatusEnum.isValid(status)) {
            return Result.buildFail("status error");
        }

        LogITaskPO logITaskPO = logITaskMapper.selectByCode(taskCode, logIJobProperties.getAppName());
        if (null == logITaskPO) {
            return Result.buildFail("task 不存在");
        }

        if (TaskStatusEnum.STOP.getValue().intValue() == status) {
            if (!jobManager.stopByTaskCode(taskCode)) {
                return Result.buildFail("stop task error");
            }
        }

        if(TaskStatusEnum.RUNNING.getValue() == status){
            execute(logITaskPO.getTaskCode(), false);
        }

        logITaskPO.setStatus(status);

        return Result.buildSucc(logITaskMapper.updateByCode(logITaskPO) > 0);
    }

    @Override
    public List<LogITask> getAllRuning() {
        List<LogITaskPO> logITaskPOList = logITaskMapper.selectRuningByAppName(logIJobProperties.getAppName());
        if (CollectionUtils.isEmpty(logITaskPOList)) {
            return new ArrayList<>();
        }

        return logITaskPOList.stream().map(p -> logITaskPO2LogITask(p)).collect(Collectors.toList());
    }

    @Override
    public List<LogITask> getPagineList(TaskPageQueryDTO queryDTO) {
        List<LogITaskPO> logITaskPOList = logITaskMapper.pagineListByCondition(logIJobProperties.getAppName(),
                queryDTO.getTaskId(), queryDTO.getTaskDesc(), queryDTO.getClassName(), queryDTO.getTaskStatus(),
                (queryDTO.getPage() - 1) * queryDTO.getSize(), queryDTO.getSize());
        if (CollectionUtils.isEmpty(logITaskPOList)) {
            return new ArrayList<>();
        }

        return logITaskPOList.stream().map(p -> logITaskPO2LogITask(p)).collect(Collectors.toList());
    }

    @Override
    public int pagineTaskConut(TaskPageQueryDTO queryDTO) {
        return logITaskMapper.pagineCountByCondition(logIJobProperties.getAppName(),
                queryDTO.getTaskId(), queryDTO.getTaskDesc(), queryDTO.getClassName(), queryDTO.getTaskStatus());
    }

    @Override
    public Result<Boolean> release(String taskCode, String workerCode) {
        // 释放锁
        Boolean lockRet = taskLockService.tryRelease(taskCode, workerCode);
        if (!lockRet) {
            return Result.buildFail("释放锁失败！");
        }

        // 更新任务状态
        boolean updateResult = updateTaskWorker(taskCode, workerCode);
        if (!updateResult) {
            return Result.buildFail("更新锁失败！");
        }
        return Result.buildSucc();
    }

    @Override
    public LogITask getByCode(String taskCode) {
        LogITaskPO logITaskPO = logITaskMapper.selectByCode(taskCode, logIJobProperties.getAppName());

        return logITaskPO2LogITask(logITaskPO);
    }

    @Override
    @Transactional
    public Result add(LogITaskDTO dto) {
        Result checkResult = checkAddParam(dto);
        if(checkResult.failed()) {
            return checkResult;
        }
        handleAdd(dto);
        return Result.buildSucc();
    }

    private void handleAdd(LogITaskDTO dto) {
        LogITaskPO logITaskPO = new LogITaskPO();
        logITaskPO.setTaskName(dto.getName());
        logITaskPO.setTaskDesc(dto.getDescription());
        logITaskPO.setCron(dto.getCron());
        logITaskPO.setClassName(dto.getClassName());
        logITaskPO.setParams(dto.getParams());
        logITaskPO.setRetryTimes(null == dto.getRetryTimes() ? RETRY_TIMES_DEFAULT_VALUE : dto.getRetryTimes());
        logITaskPO.setLastFireTime(new Timestamp(System.currentTimeMillis()));
        logITaskPO.setTimeout(TASK_TIME_OUT_DEFAULT_VALUE);
        logITaskPO.setSubTaskCodes("");
        logITaskPO.setConsensual(dto.getConsensual());
        logITaskPO.setTaskWorkerStr("");
        logITaskPO.setAppName(logIJobProperties.getAppName());
        logITaskPO.setOwner(OWNER_DEFAULT_VALUE);
        logITaskPO.setTaskCode(IdWorker.getIdStr());
        logITaskPO.setStatus(TaskStatusEnum.RUNNING.getValue());
        logITaskPO.setNodeNameWhiteListStr(dto.getNodeNameWhiteListString());
        logITaskMapper.insert(logITaskPO);
    }

    private Result checkAddParam(LogITaskDTO dto) {
        if (contains(dto.getClassName())) {
            return Result.buildFail(
                    String.format(
                            "task add failed, duplicate className: %s",
                            dto.getClassName()
                    )
            );
        }
        Class taskClazz = null;
        try {
            taskClazz = Class.forName(dto.getClassName());
        } catch (ClassNotFoundException ex) {
            return Result.buildFail(
                    String.format(
                            "task add failed, class not found: %s",
                            dto.getClassName()
                    )
            );
        }
        if(null == taskClazz) {
            return Result.buildFail(
                    String.format(
                            "task add failed, class not found: %s",
                            dto.getClassName()
                    )
            );
        }
        if(!CronExpression.isValidExpression(dto.getCron())) {
            return Result.buildFail(
                    String.format(
                            "task add failed, cron is invalid: %s",
                            dto.getCron()
                    )
            );
        }
        if(StringUtils.isEmpty(dto.getConsensual())) {
            return Result.buildFail(
                    String.format(
                            "task add failed, consensual not be null: %s",
                            JSON.toJSONString(dto)
                    )
            );
        }
        if(
                !dto.getConsensual().equals(ConsensualEnum.RANDOM.name()) &&
                        !dto.getConsensual().equals(ConsensualEnum.BROADCAST.name())
        ) {
            return Result.buildFail(
                    String.format(
                            "task add failed, consensual must be : %s",
                            "RANDOM or BROADCAST"
                    )
            );
        }
        if(!StringUtils.isEmpty(dto.getParams())) {
            try {
                Map<String, String> params = JSON.parseObject(dto.getParams(), Map.class);
                if(CollectionUtils.isEmpty(params)) {
                    return Result.buildFail(
                            "task add failed, params must be json of Map"
                    );
                }
            } catch (Exception ex) {
                return Result.buildFail(
                        "task add failed, params must be json of Map"
                );
            }
        }
        if(!StringUtils.isEmpty(dto.getNodeNameWhiteListString())) {
            try {
                List<String> nodeNameWhiteList = JSON.parseObject(dto.getNodeNameWhiteListString(), List.class);
                if(CollectionUtils.isEmpty(nodeNameWhiteList)) {
                    return Result.buildFail(
                            "task add failed, nodeNameWhiteListString must be json of List"
                    );
                }
            } catch (Exception ex) {
                return Result.buildFail(
                        "task add failed, nodeNameWhiteListString must be json of List"
                );
            }
        }

        return Result.buildSucc();
    }

    private boolean contains(String className) {
        LogITaskPO logITaskPO = logITaskMapper.selectByAppNameAndClassName(logIJobProperties.getAppName(), className);
        return null != logITaskPO;
    }

    @Override
    public Result<Boolean> copy(String sourceTaskCode, String newTaskDesc, List<String> workerIps, String param) {
        LogITaskPO logITaskPO = logITaskMapper.selectByCode(sourceTaskCode, logIJobProperties.getAppName());
        if(null == logITaskPO){
            return Result.buildFail("task 不存在");
        }

        if(CollectionUtils.isEmpty(workerIps)){
            return Result.buildFail("workerIps 为空");
        }

        Map<String, LogIWorkerPO> logIWorkerMap = workerManager.mapAllWorkers();
        List<LogITask.TaskWorker> taskWorkers   = new ArrayList<>();

        for(String ip : workerIps){
            LogITask.TaskWorker worker = new LogITask.TaskWorker();
            worker.setStatus(TaskWorkerStatusEnum.WAITING.getValue());
            worker.setIp(ip);
            worker.setWorkerCode(logIWorkerMap.getOrDefault(ip, new LogIWorkerPO()).getWorkerCode());
            taskWorkers.add(worker);
        }

        logITaskPO.setTaskDesc(newTaskDesc);
        logITaskPO.setTaskCode(sourceTaskCode + "-" + IdWorker.getIdStr());
        logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
        logITaskPO.setParams(param);

        return Result.buildSucc(logITaskMapper.insert(logITaskPO) > 0);
    }

    @Override
    public Result<Boolean> updateWorkIpsParam(String taskCode, List<String> workerIps, String param){
        LogITaskPO logITaskPO = logITaskMapper.selectByCode(taskCode, logIJobProperties.getAppName());
        if(null == logITaskPO){
            return Result.buildFail("task 不存在");
        }

        if(!CollectionUtils.isEmpty(workerIps)){
            Map<String, LogIWorkerPO> logIWorkerMap = workerManager.mapAllWorkers();
            List<LogITask.TaskWorker> taskWorkers   = new ArrayList<>();

            for(String ip : workerIps){
                LogITask.TaskWorker worker = new LogITask.TaskWorker();
                worker.setStatus(TaskWorkerStatusEnum.WAITING.getValue());
                worker.setIp(ip);
                worker.setWorkerCode(logIWorkerMap.getOrDefault(ip, new LogIWorkerPO()).getWorkerCode());
                taskWorkers.add(worker);
            }

            logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
        }

        logITaskPO.setParams(param);
        return Result.buildSucc(logITaskMapper.updateByCode(logITaskPO) > 0);
    }

    /**************************************** private method ****************************************************/
    private void executeInternal(LogITask logITask, Boolean executeSubs) {
        // jobManager 将job管理起来，超时退出抛异常
        final Future<Object> jobFuture = jobManager.start(logITask);
        if (jobFuture == null || !executeSubs) {
            return;
        }
        // 等待父任务运行完
        while (!jobFuture.isDone()) {
            ThreadUtil.sleep(WAIT_INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
        // 递归拉起子任务
        if (!StringUtils.isEmpty(logITask.getSubTaskCodes())) {
            String[] subTaskCodeArray = logITask.getSubTaskCodes().split(",");
            List<LogITaskPO> subTasks = logITaskMapper
                    .selectByCodes(Arrays.asList(subTaskCodeArray), logIJobProperties.getAppName());
            List<LogITask> subLogITaskList = subTasks.stream().map(logITaskPO -> BeanUtil.convertTo(logITaskPO,
                    LogITask.class)).collect(Collectors.toList());
            for (LogITask subLogITask : subLogITaskList) {
                execute(subLogITask, executeSubs);
            }
        }
    }

    private boolean updateTaskWorker(String taskCode, String workerCode) {
        LogITaskPO logITaskPO = logITaskMapper.selectByCode(taskCode, logIJobProperties.getAppName());
        if (logITaskPO == null) {
            return false;
        }

        List<LogITask.TaskWorker> taskWorkers = BeanUtil.convertToList(logITaskPO.getTaskWorkerStr(),
                LogITask.TaskWorker.class);
        boolean needUpdate = false;
        if (!CollectionUtils.isEmpty(taskWorkers)) {
            for (LogITask.TaskWorker taskWorker : taskWorkers) {
                if (Objects.equals(taskWorker.getWorkerCode(), workerCode)
                        && Objects.equals(taskWorker.getStatus(), TaskWorkerStatusEnum.RUNNING.getValue())) {
                    needUpdate = true;
                    taskWorker.setStatus(TaskWorkerStatusEnum.WAITING.getValue());
                }
            }
        }

        if (needUpdate) {
            logITaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
            int updateResult = logITaskMapper.updateTaskWorkStrByCode(logITaskPO);
            if (updateResult <= 0) {
                return false;
            }
        }
        return true;
    }

    private LogITask logITaskPO2LogITask(LogITaskPO logITaskPO) {
        LogITask logITask = BeanUtil.convertTo(logITaskPO, LogITask.class);
        List<LogITask.TaskWorker> taskWorkers = Lists.newArrayList();
        if (!StringUtils.isEmpty(logITaskPO.getTaskWorkerStr())) {
            List<LogITask.TaskWorker> tmpTaskWorkers = BeanUtil.convertToList(
                    logITaskPO.getTaskWorkerStr(), LogITask.TaskWorker.class);
            if (!CollectionUtils.isEmpty(tmpTaskWorkers)) {
                taskWorkers = tmpTaskWorkers;
            }
        }
        logITask.setTaskWorkers(taskWorkers);

        return logITask;
    }
}
