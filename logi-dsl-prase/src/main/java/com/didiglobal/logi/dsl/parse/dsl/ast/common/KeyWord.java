package com.didiglobal.logi.dsl.parse.dsl.ast.common;

public abstract class KeyWord extends Node {
    private String name;

    public KeyWord(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
