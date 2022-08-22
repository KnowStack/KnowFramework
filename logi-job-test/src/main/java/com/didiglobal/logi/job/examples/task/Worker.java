package com.didiglobal.logi.job.examples.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Worker {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    public void doWork() {
        logger.info("do work().");
    }

}
