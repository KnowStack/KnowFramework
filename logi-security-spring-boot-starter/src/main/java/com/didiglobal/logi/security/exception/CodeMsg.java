package com.didiglobal.logi.security.exception;

/**
 * @author jmcai
 * @version 1.0
 * @date 2021/2/17 13:13
 */
public interface CodeMsg {

    /**
     * 返回code
     * @return taskCode
     */
    Integer getCode();

    /**
     * 返回msg
     * @return msg
     */
    String getMessage();
}
