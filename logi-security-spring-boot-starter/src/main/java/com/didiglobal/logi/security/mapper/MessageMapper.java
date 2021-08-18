package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.Message;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.po.MessagePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessagePO> {
}
