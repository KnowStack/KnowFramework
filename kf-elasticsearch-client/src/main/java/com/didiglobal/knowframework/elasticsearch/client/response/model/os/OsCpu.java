package com.didiglobal.knowframework.elasticsearch.client.response.model.os;

import com.alibaba.fastjson.annotation.JSONField;

public class OsCpu {
    @JSONField(name = "percent")
    private int percent;
    @JSONField(name = "load_average")
    private LoadAverage loadAverage;

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public LoadAverage getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(LoadAverage loadAverage) {
        this.loadAverage = loadAverage;
    }
}
