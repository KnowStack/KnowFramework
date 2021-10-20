

package com.didiglobal.logi.elasticsearch.client.response.batch;

import com.alibaba.fastjson.annotation.JSONField;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;

import java.util.List;

public class ESBatchResponse extends ESActionResponse {
    @JSONField(name = "took")
    private Long took;

    @JSONField(name = "errors")
	private Boolean errors;

    @JSONField(name = "items")
	private List<IndexResultItemNode> items;

    public ESBatchResponse() {

    }

    public ESBatchResponse(List<IndexResultItemNode> items, Long took) {
        this.items = items;
        this.took = took;
    }

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public Boolean getErrors() {
        return errors;
    }

    public void setErrors(Boolean errors) {
        this.errors = errors;
    }

    public List<IndexResultItemNode> getItems() {
        return items;
    }

    public void setItems(List<IndexResultItemNode> items) {
        this.items = items;
    }


    public boolean hasFailures() {
        for (IndexResultItemNode response : items) {
            if (response.isFailed()) {
                return true;
            }
        }
        return false;
    }

    public String buildFailureMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("failure in bulk execution:");
        for (int i = 0; i < items.size(); i++) {
            IndexResultItemNode response = items.get(i);
            if (response.isFailed()) {
                IndexResultNode node = response.getIndex();
                sb.append("\n[").append(i)
                        .append("]: index [").append(node.getIndex()).append("], type [").append(node.getType()).append("], id [").append(node.getId())
                        .append("], message [").append(node.getFailureMessage()).append("]");
            }
        }
        return sb.toString();
    }
}
