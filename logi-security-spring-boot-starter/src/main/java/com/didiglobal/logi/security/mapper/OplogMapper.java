package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.common.po.OplogPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface OplogMapper extends BaseMapper<OplogPO> {
}