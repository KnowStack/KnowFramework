package com.didiglobal.logi.elasticsearch.client.response.model.os;

import com.alibaba.fastjson.annotation.JSONField;

public class LoadAverage {

    @JSONField(name = "1m")
    private int oneM;
    @JSONField(name = "5m")
    private int fiveM;
    @JSONField(name = "15m")
    private int fifteenM;

    public int getOneM() {
        return oneM;
    }

    public void setOneM(int oneM) {
        this.oneM = oneM;
    }

    public int getFiveM() {
        return fiveM;
    }

    public void setFiveM(int fiveM) {
        this.fiveM = fiveM;
    }

    public int getFifteenM() {
        return fifteenM;
    }

    public void setFifteenM(int fifteenM) {
        this.fifteenM = fifteenM;
    }
}
