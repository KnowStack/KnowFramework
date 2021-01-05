package com.didiglobal.logi.auvjob.rest;

import com.didiglobal.logi.auvjob.core.job.JobManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * job 的启动、暂停、job信息、job日志相关操作.
 *
 * @author dengshan
 */
@RestController
@RequestMapping(Constants.V1 + "/auv-job/job")
public class JobController {

  @Autowired
  private JobManager jobManager;

  @PutMapping("/stop")
  public Object stop(String jobCode) {
    return jobManager.stop(jobCode);
  }

  @PutMapping("/stopAll")
  public Object stopAll() {
    return jobManager.stopAll();
  }

  @GetMapping("/runningJobs")
  public Object getRunningJobs() {
    return jobManager.getJobs();
  }

  @GetMapping("/jobLogs")
  public Object getJobLogs(String taskCode,
                           @RequestParam(defaultValue = "10", required = false) Integer limit) {
    return jobManager.getJobLogs(taskCode, limit);
  }
}
