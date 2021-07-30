package com.didiglobal.logi.job.core.job;

/**
 * job.
 */
public interface Job {

  Object execute(JobContext jobContext) throws Exception;
}
