package com.didiglobal.knowframework.job.examples.task;

import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class Worker {

    private static final ILog logger = LogFactory.getLog(Worker.class);

    public void doWork() {
        logger.info("do work().");
    }

}
