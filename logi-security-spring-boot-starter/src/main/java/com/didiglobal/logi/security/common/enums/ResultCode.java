package com.didiglobal.logi.security.common.enums;

import com.didiglobal.logi.security.exception.CodeMsg;
import io.swagger.annotations.ApiModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 规定: #1表示成功
 * #1001～1999 区间表示参数错误
 * #2001～2999 区间表示用户错误
 * #3001～3999 区间表示接口异常
 */
@ApiModel(value = "返回说明")
public enum ResultCode implements CodeMsg {
    /* 成功 */
    SUCCESS(200, "成功"),

    /* 默认失败 */
    COMMON_FAIL(999, "失败"),

    /* 参数错误：1000～1999 */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    PARAM_LENGTH_ERROR(1005, "参数长度不正确"),

    /* 用户错误 */
    USER_NOT_LOGIN(2001, "用户未登录"),
    USER_ACCOUNT_EXPIRED(2002, "账号已过期"),
    USER_CREDENTIALS_ERROR(2003, "密码错误"),
    USER_CREDENTIALS_EXPIRED(2004, "密码过期"),
    USER_ACCOUNT_DISABLE(2005, "账号不可用"),
    USER_ACCOUNT_LOCKED(2006, "账号被锁定"),
    USER_ACCOUNT_NOT_EXIST(2007, "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST(2008, "账号已存在"),
    USER_ACCOUNT_USE_BY_OTHERS(2009, "账号下线"),
    USER_ACCOUNT_INSERT_FAIL(2010, "注册失败"),
    USER_PHONE_EXIST(2011, "手机号已存在"),
    EMAIL_FORMAT_ERROR(2012, "邮箱格式错误"),
    USER_EMAIL_EXIST(2013, "邮箱已存在"),
    USER_PASSWORD_DECRYPT_ERROR(2014, "密码解密出错"),

    /* 业务错误 */
    NO_PERMISSION(3001, "没有权限"),

    /* 角色错误 */
    ROLE_BUSINESS_ERROR(4001, "内部错误"),
    ROLE_NOT_EXISTS(4002, "角色不存在"),
    ROLE_USER_AUTHED(4003, "有用户已绑定该角色"),
    ROLE_ALREADY_EXIST(4004, "角色已存在"),

    /* 项目错误 */
    PROJECT_NAME_EXIST(5001, "项目名已存在"),
    PROJECT_NOT_EXISTS(5002, "项目不存在"),

    /* 操作日志错误 */
    RECORD_NOT_EXIST(6001, "操作日志不存在"),

    /* 消息错误 */
    MESSAGE_NOT_EXIST(7001, "消息不存在")
    ;

    private final Integer code;

    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 根据code获取message.
     *
     * @param code taskCode
     * @return str
     */
    public static String getMessageByCode(Integer code) {
        for (ResultCode ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
