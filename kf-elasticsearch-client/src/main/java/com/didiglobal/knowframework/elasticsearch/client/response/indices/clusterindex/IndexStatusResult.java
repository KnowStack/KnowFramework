package com.didiglobal.knowframework.elasticsearch.client.response.indices.clusterindex;

import com.alibaba.fastjson.annotation.JSONField;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

/**
 * @author 80256954
 */
public class IndexStatusResult {
    private Integer statusType;
    private String status;
    @JSONField(name = "number_of_shards")
    private Integer numberOfShards;
    @JSONField(name = "number_of_replicas")
    private Integer numberOfReplicas;
    @JSONField(name = "active_primary_shards")
    private Integer activePrimaryShards;
    @JSONField(name = "active_shards")
    private Integer activeShards;
    @JSONField(name = "relocating_shards")
    private Integer relocatingShards;
    @JSONField(name = "initializing_shards")
    private Integer initializingShards;
    @JSONField(name = "unassigned_shards")
    private Integer unassignedShards;

    public void setStatus(String status) {
        this.status = status;
        this.statusType = (int) ClusterHealthStatus.fromString(status).value();
    }

    public Integer getStatusType() {
        return statusType;
    }

    public String getStatus() {
        return status;
    }

    public Integer getNumberOfShards() {
        return numberOfShards;
    }

    public Integer getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public Integer getActivePrimaryShards() {
        return activePrimaryShards;
    }

    public Integer getActiveShards() {
        return activeShards;
    }

    public Integer getRelocatingShards() {
        return relocatingShards;
    }

    public Integer getInitializingShards() {
        return initializingShards;
    }

    public Integer getUnassignedShards() {
        return unassignedShards;
    }

    public void setStatusType(Integer statusType) {
        this.statusType = statusType;
    }

    public void setNumberOfShards(Integer numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public void setNumberOfReplicas(Integer numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public void setActivePrimaryShards(Integer activePrimaryShards) {
        this.activePrimaryShards = activePrimaryShards;
    }

    public void setActiveShards(Integer activeShards) {
        this.activeShards = activeShards;
    }

    public void setRelocatingShards(Integer relocatingShards) {
        this.relocatingShards = relocatingShards;
    }

    public void setInitializingShards(Integer initializingShards) {
        this.initializingShards = initializingShards;
    }

    public void setUnassignedShards(Integer unassignedShards) {
        this.unassignedShards = unassignedShards;
    }
}