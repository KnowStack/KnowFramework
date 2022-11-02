package com.didiglobal.knowframework.job.core.job.impl;

import com.didiglobal.knowframework.job.KfJobProperties;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.job.common.domain.KfJob;
import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.enums.JobStatusEnum;
import com.didiglobal.knowframework.job.common.enums.TaskWorkerStatusEnum;
import com.didiglobal.knowframework.job.common.po.*;
import com.didiglobal.knowframework.job.core.job.JobContext;
import com.didiglobal.knowframework.job.core.job.JobExecutor;
import com.didiglobal.knowframework.job.core.job.JobFactory;
import com.didiglobal.knowframework.job.core.job.JobManager;
import com.didiglobal.knowframework.job.core.task.TaskLockService;
import com.didiglobal.knowframework.job.mapper.*;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import com.didiglobal.knowframework.job.utils.ThreadUtil;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * job manager impl.
 *
 * @author ds
 */
@Service
public class JobManagerImpl implements JobManager {
    private static final Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);
    // 停止任务尝试次数
    private static final int TRY_MAX_TIMES = 3;
    // 停止任务每次尝试后sleep 时间 秒
    private static final int STOP_SLEEP_SECONDS = 3;

    // 续约锁提前检查时间 秒
    private static final Long CHECK_BEFORE_INTERVAL = 60L;
    // 每次给锁续约的时间 秒
    private static final Long RENEW_INTERVAL = 60L;

    private static final Long ONE_HOUR = 3600L;

    private JobFactory jobFactory;
    private KfJobMapper kfJobMapper;
    private KfJobLogMapper KFJobLogMapper;
    private KfTaskMapper kfTaskMapper;
    private KfWorkerMapper kfWorkerMapper;
    private JobExecutor jobExecutor;
    private TaskLockService taskLockService;
    private KfTaskLockMapper kfTaskLockMapper;
    private KfJobProperties kfJobProperties;

    private ConcurrentHashMap<KfJob, Future> jobFutureMap = new ConcurrentHashMap<>();

    private final Cache<String, String> execuedJob = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(1000).build();

    /**
     * construct
     * @param jobFactory job
     * @param kfJobMapper mapper
     * @param KFJobLogMapper mapper
     * @param kfTaskMapper mapper
     * @param kfWorkerMapper logIWorkerMapper
     * @param jobExecutor jobExecutor
     * @param taskLockService service
     * @param kfTaskLockMapper mapper
     * @param kfJobProperties 配置信息
     */
    @Autowired
    public JobManagerImpl(JobFactory jobFactory,
                          KfJobMapper kfJobMapper,
                          KfJobLogMapper KFJobLogMapper,
                          KfTaskMapper kfTaskMapper,
                          KfWorkerMapper kfWorkerMapper,
                          JobExecutor jobExecutor, TaskLockService taskLockService,
                          KfTaskLockMapper kfTaskLockMapper, KfJobProperties kfJobProperties) {
        this.jobFactory = jobFactory;
        this.kfJobMapper = kfJobMapper;
        this.KFJobLogMapper = KFJobLogMapper;
        this.kfTaskMapper = kfTaskMapper;
        this.kfWorkerMapper = kfWorkerMapper;
        this.jobExecutor = jobExecutor;
        this.taskLockService = taskLockService;
        this.kfTaskLockMapper = kfTaskLockMapper;
        this.kfJobProperties = kfJobProperties;
        initialize();
    }

    private void initialize() {
        new Thread(new JobFutureHandler(), "JobFutureHandler Thread").start();
        new Thread(new LockRenewHandler(), "LockRenewHandler Thread").start();
        new Thread(new LogCleanHandler(this.kfJobProperties.getLogExpire()),
                "LogCleanHandler Thread").start();
    }

    @Override
    public Future<Object> start(KfTask kfTask) {
        // 添加job信息
        KfJob kfJob = jobFactory.newJob( kfTask );
        if(null == kfJob){
            logger.error("class=JobHandler||method=start||classname={}||msg=logIJob is null", kfTask.getClassName());
            return null;
        }

        KfJobPO job = kfJob.getAuvJob();
        kfJobMapper.insert(job);

        Future jobFuture = jobExecutor.submit(new JobHandler( kfJob, kfTask ));
        jobFutureMap.put( kfJob, jobFuture);

        // 增加auvJobLog
        KfJobLogPO kfJobLogPO = kfJob.getAuvJobLog();
        KFJobLogMapper.insert( kfJobLogPO );
        return jobFuture;
    }

    @Override
    public Integer runningJobSize() {
        return jobFutureMap.size();
    }

    @Override
    public boolean stopByTaskCode(String taskCode) {
        for (Map.Entry<KfJob, Future> jobFuture : jobFutureMap.entrySet()) {
            KfJob kfJob = jobFuture.getKey();
            if (Objects.equals(taskCode, kfJob.getTaskCode())) {
                return stopJob( kfJob, jobFuture.getValue());
            }
        }
        return true;
    }

    @Override
    public boolean stopByJobCode(String jobCode) {
        for (Map.Entry<KfJob, Future> jobFuture : jobFutureMap.entrySet()) {
            KfJob kfJob = jobFuture.getKey();
            if (Objects.equals(jobCode, kfJob.getJobCode())) {
                return stopJob( kfJob, jobFuture.getValue());
            }
        }
        return true;
    }

    @Override
    public int stopAll() {
        AtomicInteger succeedNum = new AtomicInteger();

        for (Map.Entry<KfJob, Future> jobFuture : jobFutureMap.entrySet()) {
            KfJob kfJob = jobFuture.getKey();
            if (stopJob( kfJob, jobFuture.getValue())) {
                succeedNum.addAndGet(1);
            }
        }

        return succeedNum.get();
    }

    @Override
    public List<KfJob> getJobs() {
        List<KfJobPO> kfJobPOS = kfJobMapper.selectByAppName( kfJobProperties.getAppName());
        if (CollectionUtils.isEmpty( kfJobPOS )) {
            return null;
        }
        List<KfJob> kfJobDTOS = kfJobPOS.stream().map( kfJobPO -> BeanUtil.convertTo( kfJobPO, KfJob.class))
                .collect(Collectors.toList());
        return kfJobDTOS;
    }

    /**
     * job 执行线程.
     */
    class JobHandler implements Callable {

        private KfJob kfJob;

        private KfTask kfTask;

        public JobHandler(KfJob kfJob, KfTask kfTask) {
            this.kfJob = kfJob;
            this.kfTask = kfTask;
        }

        @Override
        public Object call() {
            TaskResult object = null;

            logger.info("class=JobHandler||method=call||msg=start job {} with classname {}",
                    kfJob.getJobCode(), kfJob.getClassName());

            try {
                kfJob.setStartTime(new Timestamp(System.currentTimeMillis()));
                kfJob.setStatus(JobStatusEnum.SUCCEED.getValue());
                kfJob.setResult(new TaskResult(TaskResult.RUNNING_CODE, "task job is running!"));
                kfJob.setError("");

                //开始执行，记录日志
                KfJobLogPO kfJobLogPO = kfJob.getAuvJobLog();
                KFJobLogMapper.updateByCode( kfJobLogPO );

                List<KfWorkerPO> kfWorkerPOS = kfWorkerMapper.selectByAppName( kfJobProperties.getAppName());
                List<String>        workCodes     = new ArrayList<>();

                if(CollectionUtils.isEmpty( kfWorkerPOS )){
                    workCodes.add( kfJob.getWorkerIp());
                }else {
                    workCodes.addAll( kfWorkerPOS.stream().map( KfWorkerPO::getWorkerCode).collect(Collectors.toList()));
                }

                JobContext jobContext = new JobContext( kfTask.getParams(), workCodes, kfJob.getWorkerCode());

                object = kfJob.getJob().execute(jobContext);

                kfJob.setResult(object);
                kfJob.setEndTime(new Timestamp(System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // 记录任务被打断 进程关闭/线程关闭
                kfJob.setStatus(JobStatusEnum.CANCELED.getValue());
                kfJob.setResult(new TaskResult(TaskResult.FAIL_CODE, "task job be canceld!"));
                String error = printStackTraceAsString(e);
                kfJob.setError(printStackTraceAsString(e));
                logger.error("class=JobHandler||method=call||classname={}||msg={}", kfJob.getClassName(), error);
            } catch (Exception e) {
                // 记录任务异常信息
                kfJob.setStatus(JobStatusEnum.FAILED.getValue());
                kfJob.setResult(new TaskResult(TaskResult.FAIL_CODE, "task job has exception when running!" + e));
                String error = printStackTraceAsString(e);
                kfJob.setError(printStackTraceAsString(e));
                logger.error("class=JobHandler||method=call||classname=||msg={}", kfJob.getClassName(), error);
            } finally {

                //执行完成，记录日志
                KfJobLogPO kfJobLogPO = kfJob.getAuvJobLog();
                KFJobLogMapper.updateByCode( kfJobLogPO );

                // job callback, 释放任务锁
                if (kfJob.getTaskCallback() != null) {
                    kfJob.getTaskCallback().callback( kfJob.getTaskCode());
                }
            }

            return object;
        }
    }

    /**
     * Job 执行清理线程，对超时的要主动杀死，执行完的收集信息并记录日志.
     */
    class JobFutureHandler implements Runnable {
        private static final long JOB_FUTURE_CLEAN_INTERVAL = 10L;

        public JobFutureHandler() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 间隔一段时间执行一次
                    ThreadUtil.sleep(JOB_FUTURE_CLEAN_INTERVAL, TimeUnit.SECONDS);

                    logger.info("class=JobFutureHandler||method=run||msg=check running jobs at regular "
                            + "time {}", JOB_FUTURE_CLEAN_INTERVAL);

                    // 定时轮询任务，检查状态并处理
                    jobFutureMap.forEach(((jobInfo, future) -> {
                        // job完成，信息整理
                        if (future.isDone()) {
                            reorganizeFinishedJob(jobInfo);
                            return;
                        }

                        // 超时处理
                        Long timeout = jobInfo.getTimeout();
                        if (timeout <= 0) {
                            return;
                        }

                        Long startTime = jobInfo.getStartTime().getTime();
                        Long now = System.currentTimeMillis();
                        Long between = (now - startTime) / 1000;

                        if (between > timeout && !future.isDone()) {
                            jobInfo.setStatus(JobStatusEnum.CANCELED.getValue());
                            future.cancel(true);
                        }
                    }));
                } catch (Exception e) {
                    logger.error("class=JobFutureHandler||method=run||msg=exception!", e);
                }
            }
        }
    }

    /**
     * 整理已完成的任务.
     *
     * @param kfJob logIJob
     */
    @Transactional(rollbackFor = Exception.class)
    public void reorganizeFinishedJob(KfJob kfJob) {
        // 移除记录
        jobFutureMap.remove( kfJob );

        execuedJob.put( kfJob.getTaskCode(), kfJob.getTaskCode());

        if (JobStatusEnum.CANCELED.getValue().equals( kfJob.getStatus())) {
            kfJob.setResult(new TaskResult(TaskResult.FAIL_CODE, "task job be canceld!"));
            kfJob.setError("task job be canceld!");
            KfJobLogPO kfJobLogPO = kfJob.getAuvJobLog();
            KFJobLogMapper.updateByCode( kfJobLogPO );
        }

        // 删除auvJob
        kfJobMapper.deleteByCode( kfJob.getJobCode());

        // 更新任务状态
        KfTaskPO kfTaskPO = kfTaskMapper.selectByCode( kfJob.getTaskCode(), kfJobProperties.getAppName());
        List<KfTask.TaskWorker> taskWorkers = BeanUtil.convertToList( kfTaskPO.getTaskWorkerStr(),
                KfTask.TaskWorker.class);

        long currentTime = System.currentTimeMillis();

        if (!CollectionUtils.isEmpty(taskWorkers)) {
            taskWorkers.sort((o1, o2) -> o1.getLastFireTime().after(o2.getLastFireTime()) ? 1 : -1);

            Iterator<KfTask.TaskWorker> iter = taskWorkers.iterator();
            while (iter.hasNext()) {
                KfTask.TaskWorker taskWorker = iter.next();
                if (TaskWorkerStatusEnum.WAITING.getValue().equals(taskWorker.getStatus())
                        && taskWorker.getLastFireTime().getTime() + 12 * ONE_HOUR * 1000 < currentTime) {
                    iter.remove();
                }

                if (Objects.equals(taskWorker.getWorkerCode(), WorkerSingleton.getInstance()
                        .getKfWorker().getWorkerCode())) {
                    taskWorker.setStatus(TaskWorkerStatusEnum.WAITING.getValue());
                }
            }
        }
        kfTaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
        kfTaskMapper.updateTaskWorkStrByCode( kfTaskPO );
    }

    /**
     * 锁续约线程.
     */
    class LockRenewHandler implements Runnable {
        private static final long JOB_INTERVAL = 10L;

        public LockRenewHandler() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    logger.info("class=LockRenewHandler||method=run||msg=check need renew lock at "
                            + "regular time {}", JOB_INTERVAL);

                    // 锁续约
                    List<KfTaskLockPO> kfTaskLockPOS = kfTaskLockMapper.selectByWorkerCode(WorkerSingleton
                            .getInstance().getKfWorker().getWorkerCode(), kfJobProperties.getAppName());

                    if (!CollectionUtils.isEmpty( kfTaskLockPOS )) {
                        long current = System.currentTimeMillis() / 1000;

                        for (KfTaskLockPO kfTaskLockPO : kfTaskLockPOS) {
                            long exTime = (kfTaskLockPO.getCreateTime().getTime() / 1000)
                                    + kfTaskLockPO.getExpireTime();

                            if (null != execuedJob.getIfPresent( kfTaskLockPO.getTaskCode())) {
                                // 续约
                                if (current < exTime && current > exTime - CHECK_BEFORE_INTERVAL) {
                                    logger.info("class=TaskLockServiceImpl||method=run||msg=update lock "
                                                    + "expireTime id={}, expireTime={}", kfTaskLockPO.getId(),
                                            kfTaskLockPO.getExpireTime());
                                    kfTaskLockMapper.update(
                                            kfTaskLockPO.getId(),
                                            kfTaskLockPO.getExpireTime() + RENEW_INTERVAL);
                                }
                                continue;
                            }

                            // 否则，删除无效的锁、过期的锁
                            if(current > exTime){
                                logger.info("class=TaskLockServiceImpl||method=run||msg=lock clean "
                                        + "lockInfo={}", BeanUtil.convertToJson( kfTaskLockPO ));
                                kfTaskLockMapper.deleteById( kfTaskLockPO.getId());
                            }


                            // 更新当前worker任务状态
                            KfTaskPO kfTaskPO = kfTaskMapper
                                    .selectByCode( kfTaskLockPO.getTaskCode(), kfJobProperties.getAppName());
                            if (kfTaskPO != null) {
                                List<KfTask.TaskWorker> taskWorkers = BeanUtil.convertToList(
                                        kfTaskPO.getTaskWorkerStr(), KfTask.TaskWorker.class);

                                if (!CollectionUtils.isEmpty(taskWorkers)) {
                                    for (KfTask.TaskWorker taskWorker : taskWorkers) {
                                        if (Objects.equals(taskWorker.getWorkerCode(), WorkerSingleton.getInstance()
                                                .getKfWorker().getWorkerCode())) {
                                            taskWorker.setStatus(TaskWorkerStatusEnum.WAITING.getValue());
                                        }
                                    }
                                }
                                kfTaskPO.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));

                                logger.info("class=TaskLockServiceImpl||method=run||msg=update task workers "
                                        + "status taskInfo={}", BeanUtil.convertToJson( kfTaskPO ));

                                kfTaskMapper.updateTaskWorkStrByCode( kfTaskPO );
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("class=LockRenewHandler||method=run||msg=exception!", e);
                }

                ThreadUtil.sleep(JOB_INTERVAL, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 定时清理日志.
     */
    class LogCleanHandler implements Runnable {
        // 每小时执行一次
        private static final long JOB_LOG_DEL_INTERVAL = 3600L;
        // 日志保存时间[默认保存7天]
        private Integer logExpire = 7;

        public LogCleanHandler(Integer logExpire) {
            if (logExpire != null) {
                this.logExpire = logExpire;
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 间隔一段时间执行一次
                    ThreadUtil.sleep( JOB_LOG_DEL_INTERVAL, TimeUnit.SECONDS);

                    logger.info("class=LogCleanHandler||method=run||msg=clean auv_job_log regular"
                            + " time {}", JOB_LOG_DEL_INTERVAL );

                    String    appName    = kfJobProperties.getAppName();
                    Timestamp deleteTime = new Timestamp(System.currentTimeMillis() - logExpire * 24 * 3600 * 1000);

                    int deleteRowTotal    = KFJobLogMapper.selectCountByAppNameAndCreateTime(appName, deleteTime);
                    int deleteRowPerTimes = deleteRowTotal / 60;
                    int deleteRowReal     = 0;

                    for(int i = 0; i < 60; i++){
                        // 删除日志
                        int count = KFJobLogMapper.deleteByCreateTime(deleteTime, appName, deleteRowPerTimes);
                        deleteRowReal += count;
                    }

                    logger.info("class=LogCleanHandler||method=run||msg=clean log deleteRowTotal={}, deleteRowReal={}",
                            deleteRowTotal, deleteRowReal);
                } catch (Exception e) {
                    logger.error("class=LogCleanHandler||method=run||msg=exception", e);
                }
            }
        }
    }

    private boolean stopJob(KfJob kfJob, Future future) {
        int tryTime = 0;
        while (tryTime < TRY_MAX_TIMES) {
            if (future.isDone()) {
                kfJob.setStatus(JobStatusEnum.CANCELED.getValue());
                if (kfJob.getTaskCallback() != null) {
                    kfJob.getTaskCallback().callback( kfJob.getTaskCode());
                }
                reorganizeFinishedJob( kfJob );
                return true;
            }
            future.cancel(true);
            tryTime++;
            ThreadUtil.sleep(STOP_SLEEP_SECONDS, TimeUnit.SECONDS);
        }

        return false;
    }

    private String printStackTraceAsString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String error = stringWriter.toString();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return timestamp.toString() + "  " + error;
    }
}
