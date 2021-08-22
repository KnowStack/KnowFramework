package com.didiglobal.logi.security.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.po.OplogPO;

/**
 * @author cjm
 */
public interface OplogDao {

    /**
     * 根据指定条件分页查询，不查询操作详情
     * @param queryDTO 查询条件
     * @return 操作日志Page
     */
    IPage<OplogPO> selectPageWithoutDetail(OplogQueryDTO queryDTO);

    /**
     * 根据操作日志id查询
     * @param oplogId 操作日志id
     * @return 操作日志
     */
    OplogPO selectByOplogId(Integer oplogId);

    /**
     * 新增操作日志
     * @param oplogPO 操作日志
     */
    void insert(OplogPO oplogPO);
}
