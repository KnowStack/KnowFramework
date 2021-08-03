package com.didiglobal.logi.security.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.resource.ManageByResourceVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分页数据统一返回规范
 *
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "分页统一返回格式")
public class PagingResult<T> extends BaseResult {

    @ApiModelProperty(value = "返回分页基本信息")
    private PagingData<T> data;

    private PagingResult(Integer code) {
        this.code = code;
    }

    private PagingResult(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public static <T> PagingResult<T> success(PagingData<T> data) {
        PagingResult<T> ret = new PagingResult<T>(ResultCode.SUCCESS.getCode());
        ret.setMessage(ResultCode.SUCCESS.getMessage());
        ret.setData(data);
        return ret;
    }

    public static <T> PagingResult<T> success() {
        return new PagingResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static <T> PagingResult<T> fail(ResultCode resultCode) {
        PagingResult<T> ret = new PagingResult<T>(resultCode.getCode());
        ret.setMessage(resultCode.getMessage());
        return ret;
    }

    public static <T> PagingResult<T> fail(Integer code, String msg) {
        PagingResult<T> ret = new PagingResult<T>(code);
        ret.setMessage(msg);
        return ret;
    }

    public static <T> PagingResult<T> fail(String msg) {
        PagingResult<T> ret = new PagingResult<T>(ResultCode.COMMON_FAIL.getCode());
        ret.setMessage(msg);
        return ret;
    }
}