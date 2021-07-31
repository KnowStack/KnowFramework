package com.didiglobal.logi.security.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页信息
 *
 * @author cjm
 */
@Data
@ApiModel
public class PagingData<T> {

    @ApiModelProperty(value = "返回数据")
    private List<T> bizData;

    @ApiModelProperty(value = "分页信息")
    private Pagination pagination;

    @Data
    @Builder
    @ApiModel(description = "分页基本信息")
    public static class Pagination {
        @ApiModelProperty(value = "总记录数")
        private long total;

        @ApiModelProperty(value = "页面总数")
        private long pages;

        @ApiModelProperty(value = "当前页码")
        private long pageNo;

        @ApiModelProperty(value = "单页大小")
        private long pageSize;
    }

    public static Pagination buildPagination(long total, long pages, long pageNo, long pageSize) {
        return Pagination.builder()
                .total(total).pages(pages).pageNo(pageNo).pageSize(pageSize)
                .build();
    }

}
