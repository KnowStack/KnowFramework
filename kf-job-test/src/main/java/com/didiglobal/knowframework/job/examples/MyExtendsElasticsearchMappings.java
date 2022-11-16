package com.didiglobal.knowframework.job.examples;

import com.didiglobal.knowframework.log.log4j2.appender.ExtendsElasticsearchMappings;
import java.util.HashMap;
import java.util.Map;

public class MyExtendsElasticsearchMappings implements ExtendsElasticsearchMappings {

    @Override
    public Map<String, String> getExtendsElasticsearchMappings() {
        Map<String, String> extendsMappings = new HashMap<>();
        extendsMappings.put("docs", "double");
        extendsMappings.put("docType", "keyword");
        return extendsMappings;
    }

}
