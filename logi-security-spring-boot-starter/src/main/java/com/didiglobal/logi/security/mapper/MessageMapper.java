package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.Message;
import com.didiglobal.logi.security.common.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
