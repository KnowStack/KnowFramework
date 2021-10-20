package com.didiglobal.logi.elasticsearch.client.response.setting.common;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.model.type.ESVersion;

public class TypeDefine {
    private JSONObject define;

    public TypeDefine(JSONObject root) {
        this.define = root;
    }

    public JSONObject toJson() {
        return define;
    }

    public JSONObject toJson(ESVersion version) {
        return TypeDefineOperator.toJson(define, version);
    }

    public void setDefine(JSONObject define) {
        this.define = define;
    }

    public JSONObject getDefine() {
        return define;
    }


    public boolean isNotOptimze() {
        return TypeDefineOperator.isNotOptimze(define);
    }


    public String getType() {
        return TypeDefineOperator.getType(define);
    }

    public boolean isIndexOff() {
        return TypeDefineOperator.isIndexOff(define);
    }


    public void setIndexOff() {
        TypeDefineOperator.setIndexOff(define);
    }


    public void setIndexOn() {
        TypeDefineOperator.setIndexOn(define);
    }

    public boolean isDocValuesOff() {
        return TypeDefineOperator.isDocValuesOff(define);
    }


    public void setDocValuesOff() {
        TypeDefineOperator.setDocValuesOff(define);
    }


    public void setDocValuesOn() {
        TypeDefineOperator.setDocValuesOn(define);
    }

    @Override
    public boolean equals(Object obj) {
        return TypeDefineOperator.isEquals(define, obj);
    }
}
