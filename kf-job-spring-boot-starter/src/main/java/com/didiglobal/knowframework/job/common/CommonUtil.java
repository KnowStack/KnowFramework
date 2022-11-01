package com.didiglobal.knowframework.job.common;

import org.springframework.util.StringUtils;

public class CommonUtil {

    /**
     * 避免模糊查询把查询条件中的"% _"当作通配符处理（造成结果是全量查询）
     */
    public static String sqlFuzzyQueryTransfer(String str){
        if(!StringUtils.isEmpty(str) && str.contains("%")){
            str = str.replaceAll("%", "\\\\%");
        }
        if(!StringUtils.isEmpty(str) && str.contains("_")){
            str = str.replaceAll("_","\\\\_");
        }
        return str;
    }
}
