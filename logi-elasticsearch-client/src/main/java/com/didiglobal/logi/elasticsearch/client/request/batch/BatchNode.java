package com.didiglobal.logi.elasticsearch.client.request.batch;

import com.alibaba.fastjson.JSONObject;

public class BatchNode {
    private BatchType batchType;

    private String index;
    private String type;
    private String id;
    private String content;

    private String routing;

    private String parent;

    public BatchNode(BatchType batchType, String index, String type, String id, String content) {
        this.batchType = batchType;
        this.index = index;
        this.type = type;
        this.id = id;
        this.content = content;
    }

    public String toMessage() throws Exception {
        String header = null;
        if(index==null) {
            throw new Exception("index is null");
        }

        if(content==null) {
            throw new Exception("content is null");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_index", index);
        jsonObject.put("_type", type);
        if (id != null) {
            jsonObject.put("_id", id);
        }

        if (routing != null) {
            jsonObject.put("_routing", routing);
        }

        if (parent != null) {
            jsonObject.put("_parent", parent);
        }

        JSONObject outter = new JSONObject();
        outter.put(batchType.getStr(), jsonObject);
        header = outter.toJSONString();
        header += "\n";

        switch (batchType) {
            case INDEX:
                return header + content.trim() + "\n";

            case CREATE:
                return header + content.trim() + "\n";

            case DELETE:
                return header;

            case UPDATE:
                return header + String.format("{\"doc\":%s,\"upsert\":%s}\n", content, content);

            default:
                throw new Exception("unknow type, type:" + batchType);
        }

    }


    public BatchType getBatchType() {
        return batchType;
    }

    public void setBatchType(BatchType batchType) {
        this.batchType = batchType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getRouting() {
        return routing;
    }

    public String getParent() {
        return parent;
    }
}
