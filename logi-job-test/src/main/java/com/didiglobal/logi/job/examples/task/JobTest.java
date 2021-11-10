//package com.didiglobal.logi.job.examples.task;
//
//import com.didiglobal.logi.job.annotation.Task;
//import com.didiglobal.logi.job.common.TaskResult;
//import com.didiglobal.logi.job.core.job.Job;
//import com.didiglobal.logi.job.core.job.JobContext;
//import com.didiglobal.logi.job.utils.CronExpression;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.sql.Timestamp;
//
//@Component
//@Task(name = "cc", description = "hello cc", cron = "0 0/1 * * * ?", autoRegister = true, timeout = 10)
//public class JobTest implements Job {
//    private static final Logger logger = LoggerFactory.getLogger(JobTest.class);
//
//    @Override
//    public TaskResult execute(JobContext jobContext) throws Exception {
//
//        Timestamp lastFireTime = new Timestamp(1636353429553L);
//
//        CronExpression cronExpression = new CronExpression("0 0/1 * * * ?");
//        long nextTime = cronExpression.getNextValidTimeAfter(lastFireTime).getTime();
//
//        System.out.println("hello world" + nextTime);
//
//        long threadid = Thread.currentThread().getId();
//
//        for (int i = 0; i < 40000000; i++) {
////      ThreadUtil.sleep(1, TimeUnit.SECONDS);
//            logger.info("hihi：" + i);
//            System.out.println("hello world:" + threadid);
//        }
//
////        throw new NullPointerException();
//
//    return new TaskResult(TaskResult.FAIL_CODE, "sdfsdfsdfdsfsdfdsfsdfsfsfsfs");
//    }
//
//}