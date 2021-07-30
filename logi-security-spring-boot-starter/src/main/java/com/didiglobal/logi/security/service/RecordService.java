package com.didiglobal.logi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.vo.record.RecordQueryVo;
import com.didiglobal.logi.security.common.vo.record.RecordTypeVo;
import com.didiglobal.logi.security.common.vo.record.RecordVo;
import com.didiglobal.logi.security.common.vo.record.TargetTypeVo;

import java.util.List;

/**
 * @author cjm
 */
public interface RecordService {

    /**
     * 分页查询操作日志
     * @param queryVo 查询条件
     * @return 分页信息
     */
    IPage<RecordVo> getPageRecord(RecordQueryVo queryVo);

    /**
     * 根据id查找操作日志详情
     * @param recordId 操作日志id
     * @return RecordVo 详情
     */
    RecordVo getDetailById(Integer recordId);

    /**
     * 获取所有操作类型
     * @return 操作类型信息
     */
    List<RecordTypeVo> getRecordTypeList();

    /**
     * 获取所有操作对象类型
     * @return 操作对象类型信息
     */
    List<TargetTypeVo> getTargetTypeList();
}
