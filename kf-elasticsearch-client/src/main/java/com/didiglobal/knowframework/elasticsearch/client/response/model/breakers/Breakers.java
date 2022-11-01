package com.didiglobal.knowframework.elasticsearch.client.response.model.breakers;

import com.alibaba.fastjson.annotation.JSONField;

public class Breakers {
    @JSONField(name = "request")
    private BreakerNode request;

    @JSONField(name = "fielddata")
    private BreakerNode fielddata;

    @JSONField(name = "parent")
    private BreakerNode parent;

    @JSONField(name = "in_flight_requests")
    private BreakerNode inFlightRequests;

    @JSONField(name = "accounting")
    private BreakerNode accounting;

    @JSONField(name = "in_flight_http_requests")
    private BreakerNode inFlightHttpRequests;

    public BreakerNode getRequest() {
        return request;
    }

    public void setRequest(BreakerNode request) {
        this.request = request;
    }

    public BreakerNode getFielddata() {
        return fielddata;
    }

    public void setFielddata(BreakerNode fielddata) {
        this.fielddata = fielddata;
    }

    public BreakerNode getParent() {
        return parent;
    }

    public void setParent(BreakerNode parent) {
        this.parent = parent;
    }

    public BreakerNode getInFlightRequests() {
        return inFlightRequests;
    }

    public void setInFlightRequests(BreakerNode inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    public BreakerNode getAccounting() {
        return accounting;
    }

    public void setAccounting(BreakerNode accounting) {
        this.accounting = accounting;
    }

    public BreakerNode getInFlightHttpRequests() {
        return inFlightHttpRequests;
    }

    public void setInFlightHttpRequests(BreakerNode inFlightHttpRequests) {
        this.inFlightHttpRequests = inFlightHttpRequests;
    }
}