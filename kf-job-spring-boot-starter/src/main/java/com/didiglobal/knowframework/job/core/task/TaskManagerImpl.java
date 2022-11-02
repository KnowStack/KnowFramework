package com.didiglobal.knowframework.job.core.task;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.dto.KfTaskDTO;
import com.didiglobal.knowframework.job.common.dto.KfTaskPageQueryDTO;
import com.didiglobal.knowframework.job.common.enums.TaskWorkerStatusEnum;
import com.didiglobal.knowframework.job.core.job.JobManager;
import com.didiglobal.knowframework.job.KfJobProperties;
import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.enums.TaskStatusEnum;
import com.didiglobal.knowframework.job.common.po.KfTaskPO;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.core.consensual.Consensual;
import com.didiglobal.knowframework.job.core.consensual.ConsensualEnum;
import com.didiglobal.knowframework.job.core.consensual.ConsensualFactory;
import com.didiglobal.knowframework.job.mapper.KfTaskMapper;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import com.didiglobal.knowframework.job.utils.CronExpression;
import com.didiglobal.knowframework.job.utils.ThreadUtil;
import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static com.didiglobal.knowframework.job.core.monitor.SimpleTaskMonitor.SCAN_INTERVAL_SLEEP_SECONDS;

/**
 * task manager impl.
 *
 * @author ds
 */
@Service
public class TaskManagerImpl implements TaskManager {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

    private static final long WAIT_INTERVAL_SECONDS = 10L;

    private JobManager jobManager;
    private ConsensualFactory consensualFactory;
    private TaskLockService taskLockService;
    private KfTaskMapper kfTaskMapper;
    private KfJobProperties kfJobProperties;


    /**
     * constructor.
     *
     * @param jobManager      jobManager
     * @param taskLockService taskLockService
     * @param kfTaskMapper  logITaskMapper
     * @param kfJobProperties 配置信息
     * @param consensualFactory 一致协议工厂
     */
    public TaskManagerImpl(JobManager jobManager, ConsensualFactory consensualFactory,
                           TaskLockService taskLockService, KfTaskMapper kfTaskMapper,
                           KfJobProperties kfJobProperties) {
        this.jobManager = jobManager;
        this.consensualFactory = consensualFactory;
        this.taskLockService = taskLockService;
        this.kfTaskMapper = kfTaskMapper;
        this.kfJobProperties = kfJobProperties;
    }

    @Override
    public Result delete(String taskCode) {
        KfTaskPO kfTaskPO = kfTaskMapper.selectByCode(taskCode, kfJobProperties.getAppName());
        if (kfTaskPO == null) {
            return Result.buildFail("任务不存在！");
        }
        return Result.buildSucc( kfTaskMapper.deleteByCode(taskCode, kfJobProperties.getAppName()) > 0);
    }

    @Override
    public boolean update(KfTaskDTO kfTaskDTO) {
        KfTaskPO kfTaskPO = BeanUtil.convertTo( kfTaskDTO, KfTaskPO.class);
        return kfTaskMapper.updateByCode( kfTaskPO ) > 0 ? true : false;
    }

    @Override
    public List<KfTask> nextTriggers(Long interval) {
        return nextTriggers(System.currentTimeMillis(), interval);
    }

    @Override
    public List<KfTask> nextTriggers(Long fromTime, Long interval) {
        List<KfTask> kfTaskList = getAllRuning();

        kfTaskList = kfTaskList.stream().filter( taskInfo -> {
            try {
                if(ConsensualEnum.RANDOM.name().equals(taskInfo.getConsensual())){
                    Timestamp lastFireTime = taskInfo.getLastFireTime();

                    List<KfTask.TaskWorker> taskWorkers = taskInfo.getTaskWorkers();
                    for (KfTask.TaskWorker taskWorker : taskWorkers) {
                        // 取到当前worker做进一步判断，如果没有找到证明没有执行过
                        if (Objects.equals(WorkerSingleton.getInstance().getKfWorker().getWorkerCode(),
                                taskWorker.getWorkerCode())) {
                            // 判断是否在当前worker可执行状态
                            if (!Objects.equals(taskWorker.getStatus(), TaskWorkerStatusEnum.WAITING.getValue())) {
                                logger.info("class=TaskManagerImpl||method=nextTriggers||msg=has task running! "
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
                    List<KfTask.TaskWorker> taskWorkers = taskInfo.getTaskWorkers();
                    Timestamp lastFireTime = new Timestamp(0L);

                    for (KfTask.TaskWorker taskWorker : taskWorkers) {
                        // 取到当前worker做进一步判断，如果没有找到证明没有执行过
                        if (Objects.equals(WorkerSingleton.getInstance().getKfWorker().getWorkerCode(),
                                taskWorker.getWorkerCode())) {

                            lastFireTime = taskWorker.getLastFireTime();
                        }
                    }

                    CronExpression cronExpression = new CronExpression(taskInfo.getCron());
                    long nextTime = cronExpression.getNextValidTimeAfter(lastFireTime).getTime();
                    taskInfo.setNextFireTime(new Timestamp(nextTime));

                    Timestamp timestamp = new Timestamp(fromTime + interval * 1000);

                    if(timestamp.after(new Timestamp(nextTime))){
                        if((nextTime + SCAN_INTERVAL_SLEEP_SECONDS * 1000) < fromTime
                                && fromTime < (nextTime + 2 * SCAN_INTERVAL_SLEEP_SECONDS * 1000)){
                            logger.info("class=TaskManagerImpl||method=nextTriggers||nextTime={}||fromTime={}||msg=skip broadcast duplicate trigger!",
                                    nextTime, fromTime);

                            for (KfTask.TaskWorker taskWorker : taskWorkers) {
                                if (Objects.equals(WorkerSingleton.getInstance().getKfWorker().getWorkerCode(),
                                        taskWorker.getWorkerCode())) {

                                    taskWorker.setLastFireTime(new Timestamp(nextTime));

                                    KfTaskPO kfTaskPO = BeanUtil.convertTo(taskInfo, KfTaskPO.class);
                                    kfTaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));

                                    kfTaskMapper.updateTaskWorkStrByCode( kfTaskPO );
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
        kfTaskList.sort(Comparator.comparing( KfTask::getNextFireTime));
        return kfTaskList;
    }

    @Override
    public void submit(List<KfTask> kfTaskList) {
        if (CollectionUtils.isEmpty( kfTaskList )) {
            return;
        }
        for (KfTask kfTask : kfTaskList) {
            // 不能在本工作器执行，跳过
            Consensual consensual = consensualFactory.getConsensual( kfTask.getConsensual());
            if (!consensual.canClaim( kfTask )) {
                continue;
            }
            execute( kfTask, false);
        }
    }

    /**
     * execute.
     */
    @Override
    public Result execute(String taskCode, Boolean executeSubs) {
        KfTaskPO kfTaskPO = kfTaskMapper.selectByCode(taskCode, kfJobProperties.getAppName());
        if (kfTaskPO == null) {
            return Result.buildFail("任务不存在！");
        }
        if (!taskLockService.tryAcquire(taskCode)) {
            return Result.buildFail("未能获取到执行锁！");
        }

        KfTask kfTask = kfTaskPO2KfTask( kfTaskPO );
        kfTask.setTaskCallback( code -> taskLockService.tryRelease(code));
        execute( kfTask, false);

        return Result.buildSucc();
    }

    @Override
    public void execute(KfTask kfTask, Boolean executeSubs) {
        Timestamp lastFireTime = new Timestamp(System.currentTimeMillis());

        KfTaskPO kfTaskPO = BeanUtil.convertTo( kfTask, KfTaskPO.class);
        List<KfTask.TaskWorker> taskWorkers = kfTask.getTaskWorkers();

        boolean worked = false;
        for (KfTask.TaskWorker taskWorker : taskWorkers) {
            if (Objects.equals(taskWorker.getWorkerCode(),
                    WorkerSingleton.getInstance().getKfWorker().getWorkerCode())) {
                taskWorker.setLastFireTime(lastFireTime);
                taskWorker.setStatus(TaskWorkerStatusEnum.RUNNING.getValue());
                worked = true;
                break;
            }
        }

        if (!worked) {
            taskWorkers.add(new KfTask.TaskWorker(TaskWorkerStatusEnum.RUNNING.getValue(),
                    new Timestamp(System.currentTimeMillis()),
                    WorkerSingleton.getInstance().getKfWorker().getWorkerCode(),
                    WorkerSingleton.getInstance().getKfWorker().getIp()));
        }

        kfTaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
        kfTaskPO.setLastFireTime(lastFireTime);
        // 更新任务状态，最近更新时间
        kfTaskMapper.updateByCode( kfTaskPO );

        // 执行
        executeInternal( kfTask, executeSubs);
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

        KfTaskPO kfTaskPO = kfTaskMapper.selectByCode(taskCode, kfJobProperties.getAppName());
        if (null == kfTaskPO) {
            return Result.buildFail("task 不存在");
        }

        if (TaskStatusEnum.STOP.getValue().intValue() == status) {
            if (!jobManager.stopByTaskCode(taskCode)) {
                return Result.buildFail("stop task error");
            }
        }

        if(TaskStatusEnum.RUNNING.getValue() == status){
            execute( kfTaskPO.getTaskCode(), false);
        }

        kfTaskPO.setStatus(status);

        return Result.buildSucc( kfTaskMapper.updateByCode( kfTaskPO ) > 0);
    }

    @Override
    public List<KfTask> getAllRuning() {
        List<KfTaskPO> kfTaskPOList = kfTaskMapper.selectRuningByAppName( kfJobProperties.getAppName());
        if (CollectionUtils.isEmpty( kfTaskPOList )) {
            return new ArrayList<>();
        }

        return kfTaskPOList.stream().map( p -> kfTaskPO2KfTask(p)).collect(Collectors.toList());
    }

    @Override
    public List<KfTask> getPagineList(KfTaskPageQueryDTO queryDTO) {
        List<KfTaskPO> kfTaskPOList = kfTaskMapper.pagineListByCondition( kfJobProperties.getAppName(),
                queryDTO.getTaskId(), queryDTO.getTaskDesc(), queryDTO.getClassName(), queryDTO.getTaskStatus(),
                (queryDTO.getPage() - 1) * queryDTO.getSize(), queryDTO.getSize());
        if (CollectionUtils.isEmpty( kfTaskPOList )) {
            return new ArrayList<>();
        }

        return kfTaskPOList.stream().map( p -> kfTaskPO2KfTask(p)).collect(Collectors.toList());
    }

    @Override
    public int pagineTaskConut(KfTaskPageQueryDTO queryDTO) {
        return kfTaskMapper.pagineCountByCondition( kfJobProperties.getAppName(),
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
    public KfTask getByCode(String taskCode) {
        KfTaskPO kfTaskPO = kfTaskMapper.selectByCode(taskCode, kfJobProperties.getAppName());

        return kfTaskPO2KfTask( kfTaskPO );
    }

    /**************************************** private method ****************************************************/
    private void executeInternal(KfTask kfTask, Boolean executeSubs) {
        // jobManager 将job管理起来，超时退出抛异常
        final Future<Object> jobFuture = jobManager.start( kfTask );
        if (jobFuture == null || !executeSubs) {
            return;
        }
        // 等待父任务运行完
        while (!jobFuture.isDone()) {
            ThreadUtil.sleep(WAIT_INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
        // 递归拉起子任务
        if (!StringUtils.isEmpty( kfTask.getSubTaskCodes())) {
            String[] subTaskCodeArray = kfTask.getSubTaskCodes().split(",");
            List<KfTaskPO> subTasks = kfTaskMapper
                    .selectByCodes(Arrays.asList(subTaskCodeArray), kfJobProperties.getAppName());
            List<KfTask> subKfTaskList = subTasks.stream().map( logITaskPO -> BeanUtil.convertTo(logITaskPO,
                    KfTask.class)).collect(Collectors.toList());
            for (KfTask subKfTask : subKfTaskList) {
                execute( subKfTask, executeSubs);
            }
        }
    }

    private boolean updateTaskWorker(String taskCode, String workerCode) {
        KfTaskPO kfTaskPO = kfTaskMapper.selectByCode(taskCode, kfJobProperties.getAppName());
        if (kfTaskPO == null) {
            return false;
        }

        List<KfTask.TaskWorker> taskWorkers = BeanUtil.convertToList( kfTaskPO.getTaskWorkerStr(),
                KfTask.TaskWorker.class);
        boolean needUpdate = false;
        if (!CollectionUtils.isEmpty(taskWorkers)) {
            for (KfTask.TaskWorker taskWorker : taskWorkers) {
                if (Objects.equals(taskWorker.getWorkerCode(), workerCode)
                        && Objects.equals(taskWorker.getStatus(), TaskWorkerStatusEnum.RUNNING.getValue())) {
                    needUpdate = true;
                    taskWorker.setStatus(TaskWorkerStatusEnum.WAITING.getValue());
                }
            }
        }

        if (needUpdate) {
            kfTaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
            int updateResult = kfTaskMapper.updateTaskWorkStrByCode( kfTaskPO );
            if (updateResult <= 0) {
                return false;
            }
        }
        return true;
    }

    private KfTask kfTaskPO2KfTask(KfTaskPO kfTaskPO) {
        KfTask kfTask = BeanUtil.convertTo( kfTaskPO, KfTask.class);
        List<KfTask.TaskWorker> taskWorkers = Lists.newArrayList();
        if (!StringUtils.isEmpty( kfTaskPO.getTaskWorkerStr())) {
            List<KfTask.TaskWorker> tmpTaskWorkers = BeanUtil.convertToList(
                    kfTaskPO.getTaskWorkerStr(), KfTask.TaskWorker.class);
            if (!CollectionUtils.isEmpty(tmpTaskWorkers)) {
                taskWorkers = tmpTaskWorkers;
            }
        }
        kfTask.setTaskWorkers(taskWorkers);

        return kfTask;
    }
}
