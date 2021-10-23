package com.didiglobal.logi.elasticsearch.client.response.query.query.hits;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ESHit {
    private Map<String, Object> unusedMap = new HashMap<>();

    private static final String SOURCE_STR = "_source";
    private Object source;

    public ESHit() {
    }

    public ESHit(JSONObject root, Class clazz) {
        if (root == null) {
            return;
        }

        for (String key : root.keySet()) {
            if (SOURCE_STR.equalsIgnoreCase(key)) {
                if (clazz == null) {
                    source = root.get(key);
                } else {
                    source = JSON.toJavaObject((JSON) root.get(key), clazz);
                }
            } else {
                unusedMap.put(key, root.get(key));
            }
        }

    }


    public Map<String, Object> getUnusedMap() {
        return unusedMap;
    }

    public void setUnusedMap(Map<String, Object> unusedMap) {
        this.unusedMap = unusedMap;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return toJson().toJSONString();
    }


    public JSONObject toJson() {
        JSONObject root = new JSONObject();
        for (String key : unusedMap.keySet()) {
            root.put(key, unusedMap.get(key));
        }

        if (source instanceof JSON) {
            root.put(SOURCE_STR, source);
        } else {
            root.put(SOURCE_STR, JSONObject.parseObject(JSONObject.toJSONString(source)));
        }

        return root;
    }
}
