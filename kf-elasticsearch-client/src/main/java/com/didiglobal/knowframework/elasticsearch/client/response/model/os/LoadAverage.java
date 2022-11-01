package com.didiglobal.knowframework.elasticsearch.client.response.model.os;

import com.alibaba.fastjson.annotation.JSONField;

public class LoadAverage {

    @JSONField(name = "1m")
    private double oneM;
    @JSONField(name = "5m")
    private double fiveM;
    @JSONField(name = "15m")
    private double fifteenM;

    public double getOneM() {
        return oneM;
    }

    public void setOneM(double oneM) {
        this.oneM = oneM;
    }

    public double getFiveM() {
        return fiveM;
    }

    public void setFiveM(double fiveM) {
        this.fiveM = fiveM;
    }

    public double getFifteenM() {
        return fifteenM;
    }

    public void setFifteenM(double fifteenM) {
        this.fifteenM = fifteenM;
    }
}
