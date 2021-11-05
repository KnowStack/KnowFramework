package com.didiglobal.logi.elasticsearch.client.response.model.os;

import com.alibaba.fastjson.annotation.JSONField;

public class OsNode {
    @JSONField(name = "timestamp")
    private long timestamp;
    @JSONField(name = "cpu")
    private OsCpu OsCpu;
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

    public OsCpu getOsCpu() {
        return OsCpu;
    }

    public void setOsCpu(OsCpu osCpu) {
        OsCpu = osCpu;
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
