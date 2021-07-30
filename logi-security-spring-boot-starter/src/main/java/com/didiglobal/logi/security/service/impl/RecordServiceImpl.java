package com.didiglobal.logi.security.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.entity.Record;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.enums.record.RecordPageCode;
import com.didiglobal.logi.security.common.enums.record.RecordTypeCode;
import com.didiglobal.logi.security.common.enums.record.TargetTypeCode;
import com.didiglobal.logi.security.common.vo.record.RecordQueryVo;
import com.didiglobal.logi.security.common.vo.record.RecordTypeVo;
import com.didiglobal.logi.security.common.vo.record.RecordVo;
import com.didiglobal.logi.security.common.vo.record.TargetTypeVo;
import com.didiglobal.logi.security.exception.SecurityException;
import com.didiglobal.logi.security.mapper.RecordMapper;
import com.didiglobal.logi.security.service.RecordService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordMapper recordMapper;

    @Override
    public IPage<RecordVo> getPageRecord(RecordQueryVo queryVo) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<Record> recordPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 不查找detail字段
        queryWrapper.select(record -> !"detail".equals(record.getColumn()));

        queryWrapper
                .eq(queryVo.getRecordType() != null, "record_type", queryVo.getRecordType())
                .eq(queryVo.getTargetType() != null, "target_type", queryVo.getTargetType())
                .ge(queryVo.getStartTime() != null, "create_time", queryVo.getStartTime())
                .le(queryVo.getEndTime() != null, "create_time", queryVo.getEndTime())
                .like(queryVo.getTarget() != null, "target", queryVo.getTarget())
                .like(queryVo.getRecordIp() != null, "record_ip", queryVo.getRecordIp())
                .like(queryVo.getRecordUsername() != null, "record_username", queryVo.getRecordUsername());

        recordMapper.selectPage(recordPage, queryWrapper);

        return CopyBeanUtil.copyPage(recordPage, RecordVo.class);
    }

    @Override
    public RecordVo getDetailById(Integer recordId) {
        Record record = recordMapper.selectById(recordId);
        if(record == null) {
            throw new SecurityException(ResultCode.RECORD_NOT_EXIST);
        }
        RecordVo recordVo = CopyBeanUtil.copy(record, RecordVo.class);
        RecordPageCode recordPageCode = RecordPageCode.getByType(record.getRecordPage());
        recordVo.setRecordPage(recordPageCode.getInfo());
        RecordTypeCode recordTypeCode = RecordTypeCode.getByType(record.getRecordType());
        recordVo.setRecordType(recordTypeCode.getInfo());
        TargetTypeCode targetTypeCode = TargetTypeCode.getByType(record.getTargetType());
        recordVo.setTargetType(targetTypeCode.getInfo());

        recordVo.setCreateTime(record.getCreateTime().getTime());
        return recordVo;
    }

    @Override
    public List<RecordTypeVo> getRecordTypeList() {
        RecordTypeCode[] typeCodes = RecordTypeCode.values();
        List<RecordTypeVo> typeVoList = new ArrayList<>();
        for(RecordTypeCode typeCode : typeCodes) {
            RecordTypeVo recordTypeVo = new RecordTypeVo();
            recordTypeVo.setType(typeCode.getType());
            recordTypeVo.setInfo(typeCode.getInfo());
            typeVoList.add(recordTypeVo);
        }
        return typeVoList;
    }

    @Override
    public List<TargetTypeVo> getTargetTypeList() {
        TargetTypeCode[] typeCodes = TargetTypeCode.values();
        List<TargetTypeVo> typeVoList = new ArrayList<>();
        for(TargetTypeCode typeCode : typeCodes) {
            TargetTypeVo targetTypeVo = new TargetTypeVo();
            targetTypeVo.setType(typeCode.getType());
            targetTypeVo.setInfo(typeCode.getInfo());
            typeVoList.add(targetTypeVo);
        }
        return typeVoList;
    }
}
