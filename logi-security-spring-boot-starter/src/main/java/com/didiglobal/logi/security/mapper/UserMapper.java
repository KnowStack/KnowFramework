package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.User;
import com.didiglobal.logi.security.common.po.UserPO;
import org.apache.ibatis.annotations.*;

/**
 * @author cjm
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
