package com.didiglobal.knowframework.elasticsearch.client.response.model.process;

import com.alibaba.fastjson.annotation.JSONField;

public class ProcessMem {
    @JSONField(name = "total_virtual_in_bytes")
    private long totalVirtualInBytes;

    public long getTotalVirtualInBytes() {
        return totalVirtualInBytes;
    }

    public void setTotalVirtualInBytes(long totalVirtualInBytes) {
        this.totalVirtualInBytes = totalVirtualInBytes;
    }
}
