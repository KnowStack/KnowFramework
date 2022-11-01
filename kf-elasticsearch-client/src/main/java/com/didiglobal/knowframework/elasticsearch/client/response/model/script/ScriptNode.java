package com.didiglobal.knowframework.elasticsearch.client.response.model.script;

import com.alibaba.fastjson.annotation.JSONField;

public class ScriptNode {
    @JSONField(name = "compilations")
    private long compilations;

    @JSONField(name = "cache_evictions")
    private long cacheEvictions;

    public long getCompilations() {
        return compilations;
    }

    public void setCompilations(long compilations) {
        this.compilations = compilations;
    }

    public long getCacheEvictions() {
        return cacheEvictions;
    }

    public void setCacheEvictions(long cacheEvictions) {
        this.cacheEvictions = cacheEvictions;
    }
}
