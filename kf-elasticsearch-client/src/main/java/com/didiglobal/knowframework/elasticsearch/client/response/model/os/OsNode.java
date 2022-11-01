package com.didiglobal.knowframework.elasticsearch.client.response.model.os;

import com.alibaba.fastjson.annotation.JSONField;

public class OsNode {
    @JSONField(name = "timestamp")
    private long timestamp;
    @JSONField(name = "cpu")
    private OsCpu cpu;
    @JSONField(name = "cpu_percent")
    private Integer cpu_percent;
    @JSONField(name = "load_average")
    private Double load_average;
    @JSONField(name = "mem")
    private OsMem mem;
    @JSONField(name = "swap")
    private OsSwap swap;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCpu_percent() {
        return cpu_percent;
    }

    public void setCpu_percent(Integer cpu_percent) {
        this.cpu_percent = cpu_percent;
    }

    public Double getLoad_average() {
        return load_average;
    }

    public void setLoad_average(Double load_average) {
        this.load_average = load_average;
    }

    public OsCpu getCpu() {
        if (null != cpu_percent) {
            cpu = cpu == null ? new OsCpu() : cpu;
            cpu.setPercent(cpu_percent);
            LoadAverage loadAverage = new LoadAverage();
            loadAverage.setOneM(load_average);
            loadAverage.setFiveM(load_average);
            loadAverage.setFifteenM(load_average);
            cpu.setLoadAverage(loadAverage);
        }
        return cpu;
    }

    public void setCpu(OsCpu cpu) {
        this.cpu = cpu;
    }

    public OsMem getMem() {
        return mem;
    }

    public void setMem(OsMem mem) {
        this.mem = mem;
    }

    public OsSwap getSwap() {
        return swap;
    }

    public void setSwap(OsSwap swap) {
        this.swap = swap;
    }
}
