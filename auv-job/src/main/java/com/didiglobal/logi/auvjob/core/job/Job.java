package com.didiglobal.logi.auvjob.core.job;

/**
 * @author dengshan
 */
public interface Job {

  Object execute(JobContext jobContext);
}
