package com.didiglobal.knowframework.job.examples.task;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.job.core.job.Job;
import com.didiglobal.knowframework.job.core.job.JobContext;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobBroadcasWithParamtTest implements Job {

    private static final ILog logger = LogFactory.getLog(JobBroadcasWithParamtTest.class);

    @Autowired
    private Worker worker;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("**************************************** JobBroadcasWithParamtTest start" + System.currentTimeMillis());

        System.err.println(
                String.format(
                        "params: %s, allWorkerCodes: %s, currentWorkerCode: %s",
                        jobContext.getParams(),
                        JSON.toJSONString(jobContext.getAllWorkerCodes()),
                        jobContext.getCurrentWorkerCode()
                )
        );

        worker.doWork();

        logger.info("**************************************** JobBroadcasWithParamtTest end" + System.currentTimeMillis());

        return TaskResult.buildSuccess();
    }
}