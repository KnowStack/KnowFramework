package com.didiglobal.knowframework.security.exception;

/**
 * @author cjm
 *
 * 返回后端和前端商定的code（msg前端自己决定）
 */
public class KfSecurityException extends RuntimeException {

    public KfSecurityException() {}

    public KfSecurityException(CodeMsg codeMsg) {
        super(codeMsg.getCode() + "-" + codeMsg.getMessage());
    }

    public KfSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
