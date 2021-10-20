package com.didiglobal.logi.elasticsearch.client.response.batch;

public class IndexResultItemNode {
    private IndexResultNode index;

    public IndexResultNode getIndex() {
        return index;
    }

    public void setIndex(IndexResultNode index) {
        this.index = index;
    }


    public boolean isFailed() {
        return index.getError() != null;
    }
}
