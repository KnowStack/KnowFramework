package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.TaskResult;

/**
 * job.
 */
public interface Job {

    TaskResult execute(JobContext jobContext) throws Exception;
}
