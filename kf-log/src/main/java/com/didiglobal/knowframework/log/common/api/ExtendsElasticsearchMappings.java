package com.didiglobal.knowframework.log.common.api;

import java.util.Map;

public interface ExtendsElasticsearchMappings {

    /**
     * @return 返回 elasticsearch 扩展字段集，key：字段名 value：elasticsearch 字段类型
     */
    Map<String, String> getExtendsElasticsearchMappings();

}
