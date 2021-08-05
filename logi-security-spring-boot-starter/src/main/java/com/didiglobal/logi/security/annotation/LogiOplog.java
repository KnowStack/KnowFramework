package com.didiglobal.logi.security.annotation;

import java.lang.annotation.*;

/**
 * @author cjm
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogiOplog {

    // 页面
    String operatePage();

    // 操作类型
    String operateType();

    // 对象分类
    String targetType();

    // 操作对象
    String target();
}
