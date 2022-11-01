package com.didiglobal.knowframework.security.common;

import com.didiglobal.knowframework.security.common.enums.ResultCode;
import com.didiglobal.knowframework.security.exception.LogiSecurityException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 非分页统一的返回规范
 *
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "统一返回格式")
public class Result<T> extends BaseResult {

    @ApiModelProperty(value = "返回数据")
    protected T data;

    public boolean successed() {
        return getCode() != null && ResultCode.SUCCESS.getCode().equals(getCode());
    }

    public boolean duplicate() {
        return getCode() != null && ResultCode.RESOURCE_DUPLICATION.getCode().equals(getCode());
    }

    public boolean failed() {
        return !successed();
    }

    public Result() {}

    private Result(Integer code) {
        this.code = code;
    }

    private Result(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public static <T> Result<T> build(boolean succ) {
        if (succ) {
            return success();
        }
        return fail();
    }

    public static <T> Result<T> success(T data) {
        Result<T> ret = new Result<>(ResultCode.SUCCESS.getCode());
        ret.setMessage(ResultCode.SUCCESS.getMessage());
        ret.setData(data);
        return ret;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> ret = new Result<>(resultCode.getCode());
        ret.setMessage(resultCode.getMessage());
        return ret;
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> ret = new Result<>(code);
        ret.setMessage(msg);
        return ret;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> ret = new Result<>(ResultCode.COMMON_FAIL.getCode());
        ret.setMessage(msg);
        return ret;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.COMMON_FAIL.getCode());
        result.setMessage(ResultCode.COMMON_FAIL.getMessage());
        return result;
    }

    public static <T> Result<T> fail(LogiSecurityException e) {
        String[] s = e.getMessage().split("-", 2);
        return Result.fail(Integer.parseInt(s[0]), s[1]);
    }

    public static <T> Result<T> buildSucc(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> buildFrom(Result<? extends Object> result) {
        Result<T> resultT = new Result<>();
        resultT.setCode(result.getCode());
        resultT.setMessage(result.getMessage());
        return resultT;
    }

    public static <T> Result<T> buildParamIllegal(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.PARAM_NOT_VALID.getCode());
        result.setMessage(ResultCode.PARAM_NOT_VALID.getMessage() + ":" + msg + "，请检查后再提交！");
        return result;
    }

    public static <T> Result<T> buildNotExist(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.RESOURCE_TYPE_NOT_EXISTS.getCode());
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> buildDuplicate(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.RESOURCE_DUPLICATION.getCode());
        result.setMessage(msg);
        return result;
    }
}
