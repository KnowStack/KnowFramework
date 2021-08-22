package com.didiglobal.logi.security.exception;

import com.didiglobal.logi.security.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cjm
 * 全局异常处理
 */
// @ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获SecurityException异常
     */
    @ResponseBody
    @ExceptionHandler(value = LogiSecurityException.class)
    public Result<String> securityException(LogiSecurityException e) {
        e.printStackTrace();
        String[] s = e.getMessage().split("-", 2);
        return Result.fail(Integer.parseInt(s[0]), s[1]);
    }

    /**
     * 捕获Exception异常，系统各种奇奇怪怪的异常
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result<String> exception(Exception e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }
}
