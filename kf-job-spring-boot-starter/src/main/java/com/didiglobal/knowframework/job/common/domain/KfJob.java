package com.didiglobal.knowframework.job.common.domain;

import com.didiglobal.knowframework.job.core.job.Job;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.job.common.po.KfJobPO;
import com.didiglobal.knowframework.job.common.po.KfJobLogPO;
import com.didiglobal.knowframework.job.core.task.TaskCallback;

import java.sql.Timestamp;
import java.util.Objects;

import com.didiglobal.knowframework.job.utils.BeanUtil;
import lombok.Data;

@Data
public class KfJob {
    private String jobCode;
    private String taskCode;
    private Long taskId;
    private String taskName;
    private String taskDesc;
    private String className;
    private Integer retryTimes;
    private Integer tryTimes;
    private String workerCode;
    private String workerIp;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer status;
    private String error;
    private Long timeout;
    private TaskResult result;
    private Job job;
    private TaskCallback taskCallback;
    private String appName;

    /**
     * auv job.
     *
     * @return auv job
     */
    public KfJobPO getAuvJob() {
        KfJobPO job = new KfJobPO();
        job.setJobCode(getJobCode());
        job.setTaskCode(getTaskCode());
        job.setClassName(getClassName());
        job.setTryTimes(getTryTimes());
        job.setWorkerCode(getWorkerCode());
        job.setAppName(getAppName());
        job.setStartTime(new Timestamp(System.currentTimeMillis()));
        job.setCreateTime(new Timestamp(System.currentTimeMillis()));
        job.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return job;
    }

    /**
     * auv job log.
     *
     * @return job log
     */
    public KfJobLogPO getAuvJobLog() {
        KfJobLogPO kfJobLogPO = new KfJobLogPO();
        kfJobLogPO.setJobCode(getJobCode());
        kfJobLogPO.setTaskCode(getTaskCode());
        kfJobLogPO.setTaskId(getTaskId());
        kfJobLogPO.setTaskName(getTaskName());
        kfJobLogPO.setTaskDesc(getTaskDesc());
        kfJobLogPO.setClassName(getClassName());
        kfJobLogPO.setWorkerCode(getWorkerCode());
        kfJobLogPO.setWorkerIp(getWorkerIp());
        kfJobLogPO.setTryTimes(getTryTimes());
        kfJobLogPO.setStartTime(getStartTime());
        kfJobLogPO.setEndTime(getEndTime());
        kfJobLogPO.setStatus(getStatus());
        kfJobLogPO.setError(getError() == null ? "" : getError());
        kfJobLogPO.setResult(getResult() == null ? "" : BeanUtil.convertToJson(getResult()));
        kfJobLogPO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        kfJobLogPO.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        kfJobLogPO.setAppName(this.getAppName());
        return kfJobLogPO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KfJob kfJob = (KfJob) o;
        return jobCode.equals( kfJob.jobCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobCode);
    }
}