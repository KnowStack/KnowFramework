package com.didiglobal.logi.elasticsearch.client.response.cluster.nodesstats;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.didiglobal.logi.elasticsearch.client.model.ESActionResponse;

import java.util.Map;

public class ESClusterNodesStatsResponse extends ESActionResponse {
    @JSONField(name = "cluster_name")
    private String clusterName;

    @JSONField(name = "nodes")
    private Map<String, ClusterNodeStats> nodes;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Map<String, ClusterNodeStats> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, ClusterNodeStats> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return toJson().toJSONString();
    }

    public JSONObject toJson() {
        return (JSONObject) JSONObject.toJSON(this);
    }
}
