package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.domain.LogIJob;
import com.didiglobal.knowframework.job.common.domain.LogITask;

/**
 * job factory.
 *
 * @author ds
 */
public interface JobFactory {
    void addJob(String className, Job job);

    LogIJob newJob(LogITask logITask);
}
