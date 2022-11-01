package com.didiglobal.knowframework.security.service;

import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.knowframework.security.common.dto.oplog.OplogDTO;
import com.didiglobal.knowframework.security.common.vo.oplog.OplogVO;

import java.util.List;

/**
 * @author cjm
 */
public interface OplogService {

    /**
     * 保存操作日志，并获取操作日志id
     *
     * @param oplogDTO 操作日志
     * @return 操作日志id
     */
    Integer saveOplog(OplogDTO oplogDTO);

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @return 分页信息
     */
    PagingData<OplogVO> getOplogPage(OplogQueryDTO queryDTO);

    /**
     * 根据id查找操作日志详情
     *
     * @param opLogId 操作日志id
     * @return OplogVo 详情
     */
    OplogVO getOplogDetailByOplogId(Integer opLogId);

    /**
     * 获取日志组列表
     * @return
     */
    List<String> listTargetType();
}
