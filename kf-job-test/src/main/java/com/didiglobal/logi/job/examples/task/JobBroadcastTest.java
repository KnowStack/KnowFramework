package com.didiglobal.logi.job.examples.task;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
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