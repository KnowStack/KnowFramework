package com.didiglobal.logi.elasticsearch.client.response.model.indices;

import com.alibaba.fastjson.annotation.JSONField;

public class Translog {
    @JSONField(name = "operations")
    private long operations;

    @JSONField(name = "size_in_bytes")
    private long sizeInBytes;

    @JSONField(name = "uncommitted_size_in_bytes")
    private long uncommittedSizeInBytes;

    public long getOperations() {
        return operations;
    }

    public void setOperations(long operations) {
        this.operations = operations;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public long getUncommittedSizeInBytes() {
        return uncommittedSizeInBytes;
    }

    public void setUncommittedSizeInBytes(long uncommittedSizeInBytes) {
        this.uncommittedSizeInBytes = uncommittedSizeInBytes;
    }
}
