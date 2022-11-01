package com.didiglobal.knowframework.security.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.knowframework.security.common.po.UserPO;
import org.apache.ibatis.annotations.*;

/**
 * @author cjm
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
