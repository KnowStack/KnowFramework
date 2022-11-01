package com.didiglobal.knowframework.dsl.parse.dsl.parser;

public enum ParserType {
    COMMON("root"),
    QUERY("query"),
    AGGR("aggr");

    private String value;

    ParserType(String value) {
        this.value = value;
    }
}
