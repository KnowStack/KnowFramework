package com.didiglobal.logi.elasticsearch.client.parser.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum DangerousDslTagEnum {


    AGGS_DEEP_NEST("aggs deep nest", "aggs嵌套层数过深"),

    AGGS_SIGNIFICANT_TERMS("aggs significant_terms", "aggs中带了significant_terms"),

    AGGS_CARDINALITY("aggs cardinality", "aggs中带了cardinality"),

    WITH_SCRIPT("script", "query中带了script"),

    WITH_WILDCARD_PRE("wildcard pre*", "query中带了wildcard，且前缀*号"),

    WITH_REGEXP("regexp", "query中带了regexp"),

    DSL_LENGTH_TOO_LARGE("dsl length more 5k", "查询语句超过5k");

    private final String tag;

    private final String desc;

    public String getTag() {
        return tag;
    }

    public String getDesc() {
        return desc;
    }

    DangerousDslTagEnum(String tag, String desc) {
        this.tag = tag;
        this.desc = desc;
    }

    private final static Map<String, DangerousDslTagEnum> TAG_MAP;

    static {
        Map<String, DangerousDslTagEnum> tagMap = new HashMap<>();
        for (DangerousDslTagEnum threadPoolType : DangerousDslTagEnum.values()) {
            tagMap.put(threadPoolType.getTag(), threadPoolType);
        }
        TAG_MAP = Collections.unmodifiableMap(tagMap);
    }


    public static DangerousDslTagEnum fromTag(String tag) {
        DangerousDslTagEnum dslTagEnum = TAG_MAP.get(tag);
        if (dslTagEnum == null) {
            throw new IllegalArgumentException("no dslTagEnum for " + tag);
        }
        return dslTagEnum;
    }


    public static Map<String, DangerousDslTagEnum> getTagMap() {
        return TAG_MAP;
    }

}
