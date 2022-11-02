package com.didiglobal.knowframework.job.common.domain;

import com.didiglobal.knowframework.job.common.po.KfWorkerPO;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class KfWorker {

    private String workerCode;
    private String workerName;

    private String ip;

    private Integer cpu;

    private Double cpuUsed;

    /*
     * 以M为单位
     */
    private Double memory;

    private Double memoryUsed;

    /*
     * 以M为单位
     */
    private Double jvmMemory;

    private Double jvmMemoryUsed;

    private Integer jobNum;

    private Timestamp heartbeat;

    private String appName;

    /**
     * get auv worker.
     *
     * @return auv worker
     */
    public KfWorkerPO getWorker() {
        KfWorkerPO kfWorkerPO = new KfWorkerPO();
        kfWorkerPO.setWorkerCode(this.workerCode);
        kfWorkerPO.setWorkerName(this.workerName);
        kfWorkerPO.setIp(this.getIp());
        kfWorkerPO.setCpu(this.cpu);
        kfWorkerPO.setCpuUsed(this.cpuUsed);
        kfWorkerPO.setMemory(this.memory);
        kfWorkerPO.setMemoryUsed(this.memoryUsed);
        kfWorkerPO.setJvmMemory(this.jvmMemory);
        kfWorkerPO.setJvmMemoryUsed(this.jvmMemoryUsed);
        kfWorkerPO.setJobNum(this.jobNum);
        kfWorkerPO.setHeartbeat(new Timestamp(System.currentTimeMillis()));
        kfWorkerPO.setAppName(this.appName);
        return kfWorkerPO;
    }
}