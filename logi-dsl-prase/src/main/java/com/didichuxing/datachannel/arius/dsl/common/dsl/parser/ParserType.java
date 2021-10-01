package com.didichuxing.datachannel.arius.dsl.common.dsl.parser;

public enum ParserType {
    COMMON("root"),
    QUERY("query"),
    AGGR("aggr");

    private String value;

    ParserType(String value) {
        this.value = value;
    }
}
