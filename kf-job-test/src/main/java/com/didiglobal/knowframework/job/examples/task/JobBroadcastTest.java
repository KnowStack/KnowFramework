package com.didiglobal.knowframework.job.examples.task;

import com.didiglobal.knowframework.job.core.consensual.ConsensualEnum;
import com.didiglobal.knowframework.job.core.job.Job;
import com.didiglobal.knowframework.job.core.job.JobContext;
import com.didiglobal.knowframework.job.annotation.Task;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import org.springframework.stereotype.Component;

@Component
@Task(name = "cc broad", description = "hello broad", cron = "0 0/1 * * * ? *", autoRegister = true, timeout = 300, consensual = ConsensualEnum.BROADCAST)
public class JobBroadcastTest implements Job {
    private static final ILog logger = LogFactory.getLog(JobBroadcastTest.class);

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("**************************************** JobBroadcastTest start" + System.currentTimeMillis());
        logger.info("**************************************** JobBroadcastTest end" + System.currentTimeMillis());
        return TaskResult.buildSuccess();
    }
}