package com.didiglobal.logi.security.exception;

/**
 * @author qmoj
 * @version 1.0
 * @date 2021/1/21 13:02
 *
 * 返回后端和前端商定的code（msg前端自己决定）
 */
public class SecurityException extends RuntimeException {

    public SecurityException() {}

    public SecurityException(CodeMsg codeMsg) {
        super(codeMsg.getCode() + "-" + codeMsg.getMessage());
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
