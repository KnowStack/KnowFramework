package com.didiglobal.logi.security.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.enums.ResultCode;
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

    public static <T> PagingResult<T> success(IPage<T> iPage) {
        PagingData.Pagination pagination = PagingData.Pagination.builder()
                .pageNo(iPage.getCurrent())
                .pageSize(iPage.getSize())
                .total(iPage.getTotal())
                .pages(iPage.getPages())
                .build();
        PagingData<T> pagingData = new PagingData<>();
        pagingData.setBizData(iPage.getRecords());
        pagingData.setPagination(pagination);
        PagingResult<T> ret = new PagingResult<T>();
        ret.setData(pagingData);
        ret.setCode(ResultCode.SUCCESS.getCode());
        ret.setMessage(ResultCode.SUCCESS.getMessage());
        return ret;
    }

}