package com.didiglobal.logi.auvjob.examples.task;

import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.util.concurrent.TimeUnit;

@Task(name = "cc", description = "hello cc", cron = "* */5 * * * ? ", autoRegister = true)
public class JobTest implements Job {

  @Override
  public Object execute(JobContext jobContext) {
    for (int i = 0; i < 20; i++) {
      ThreadUtil.sleep(1, TimeUnit.SECONDS);
      System.out.println("hello world");
    }
    return null;
  }
}