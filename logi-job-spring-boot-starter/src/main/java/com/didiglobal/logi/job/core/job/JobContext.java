package com.didiglobal.logi.job.core.job;

/**
 * job context.
 *
 * @author ds
 */
public class JobContext {
    private String params;
    private int    totalWorker;

    public JobContext(){}
    public JobContext(String params, int totalWorker){
        this.params         = params;
        this.totalWorker    = totalWorker;
    }
}
