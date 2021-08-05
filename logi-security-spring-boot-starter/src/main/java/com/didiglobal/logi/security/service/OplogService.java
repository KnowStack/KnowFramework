package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.OplogDto;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogTypeVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogVo;
import com.didiglobal.logi.security.common.vo.oplog.TargetTypeVo;

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
     * 获取所有操作类型
     * @return 操作类型信息
     */
    List<OplogTypeVo> getOperateTypeList();

    /**
     * 获取所有操作对象类型
     * @return 操作对象类型信息
     */
    List<TargetTypeVo> getTargetTypeList();

    /**
     * 保存操作记录
     * @param oplogDto 操作信息
     */
    void saveOplog(OplogDto oplogDto);
}
