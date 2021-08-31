package com.didiglobal.logi.job.examples.task;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualConstant;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.job.utils.ThreadUtil;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Task(name = "cc broad", description = "hello broad", cron = "0 0/1 * * * ? *", autoRegister = true,
        consensual = ConsensualConstant.BROADCAST, timeout = 300)
public class JobBroadcastTest implements Job {
  private static final Logger logger = LoggerFactory.getLogger(JobBroadcastTest.class);

  @Override
  public TaskResult execute(JobContext jobContext) {
    for (int i = 0; i < 500; i++) {
//      ThreadUtil.sleep(1, TimeUnit.SECONDS);
      logger.info("hihi broad broad");
      System.out.println("hello world broad broad");
    }
    return TaskResult.SUCCESS;
  }
}