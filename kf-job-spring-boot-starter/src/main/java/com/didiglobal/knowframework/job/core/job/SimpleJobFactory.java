package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.domain.KfJob;
import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.enums.JobStatusEnum;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * simple job factory.
 *
 * @author ds
 */
@Component
public class SimpleJobFactory implements JobFactory {

    private static final Logger logger = LoggerFactory.getLogger(SimpleJobFactory.class);

    private Map<String, Job> jobMap = new HashMap<>();

    @Override
    public void addJob(String className, Job job) {
        jobMap.put(className, job);

        logger.info("class=SimpleJobFactory||method=addJob||className={}||jobMap={}",
                className, jobMap.toString());
    }

    @Override
    public KfJob newJob(KfTask kfTask) {
        if(null == jobMap.get( kfTask.getClassName())){
            return null;
        }

        KfJob kfJob = new KfJob();
        kfJob.setJobCode(IdWorker.getIdStr());
        kfJob.setTaskCode( kfTask.getTaskCode());
        kfJob.setTaskId( kfTask.getId());
        kfJob.setTaskName( kfTask.getTaskName());
        kfJob.setTaskDesc( kfTask.getTaskDesc());
        kfJob.setClassName( kfTask.getClassName());
        kfJob.setWorkerCode(WorkerSingleton.getInstance().getKfWorker().getWorkerCode());
        kfJob.setWorkerIp(WorkerSingleton.getInstance().getKfWorker().getIp());
        kfJob.setTryTimes( kfTask.getRetryTimes() == null ? 1 : kfTask.getRetryTimes());
        kfJob.setStatus(JobStatusEnum.STARTED.getValue());
        kfJob.setTimeout( kfTask.getTimeout());
        kfJob.setJob(jobMap.get( kfTask.getClassName()));
        kfJob.setTaskCallback( kfTask.getTaskCallback());
        kfJob.setAppName( kfTask.getAppName());
        return kfJob;
    }
}
