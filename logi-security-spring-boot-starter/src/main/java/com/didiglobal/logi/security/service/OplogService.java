package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogVo;

import java.util.List;

/**
 * @author cjm
 */
public interface OplogService {

    /**
     * 分页查询操作日志
     * @param queryVo 查询条件
     * @return 分页信息
     */
    PagingData<OplogVo> getOplogPage(OplogQueryVo queryVo);

    /**
     * 根据id查找操作日志详情
     * @param opLogId 操作日志id
     * @return OplogVo 详情
     */
    OplogVo getDetailById(Integer opLogId);

    /**
     * 获取操作日志列表的查询条件信息
     * @param type 1 操作页面、2 操作类型、3 对象分类
     * @return 操作页面NameList、操作类型NameList、对象分类NameList
     */
    List<String> getOplogExtraList(Integer type);

}
