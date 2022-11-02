package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.domain.KfJob;
import com.didiglobal.knowframework.job.common.domain.KfTask;

/**
 * job factory.
 *
 * @author ds
 */
public interface JobFactory {
    void addJob(String className, Job job);

    KfJob newJob(KfTask kfTask);
}
