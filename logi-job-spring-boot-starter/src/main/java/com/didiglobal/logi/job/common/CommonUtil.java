package com.didiglobal.logi.job.common;

public class CommonUtil {

    /**
     * 避免模糊查询把查询条件中的"% _"当作通配符处理（造成结果是全量查询）
     */
    public static String sqlFuzzyQueryTransfer(String str){
        if(str.contains("%")){
            str = str.replaceAll("%", "\\\\%");
        }
        if(str.contains("_")){
            str = str.replaceAll("_","\\\\_");
        }
        return str;
    }
}
