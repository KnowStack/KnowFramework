package com.didiglobal.knowframework.elasticsearch.client.response.model.indices;

import com.alibaba.fastjson.annotation.JSONField;

public class Docs {
    @JSONField(name = "count")
    private long count;

    @JSONField(name = "deleted")
    private long deleted;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getDeleted() {
        return deleted;
    }

    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }
}
