package com.didiglobal.logi.security.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.didiglobal.logi.security.common.dto.config.ConfigQueryDTO;
import com.didiglobal.logi.security.common.po.ConfigPO;

import java.util.List;

/**
 * @author cjm
 */
public interface ConfigDao {

    int insert(ConfigPO param);

    int updateById(ConfigPO param);

    int update(ConfigPO param);

    int deleteById(Integer id);

    IPage<ConfigPO> selectPage(ConfigQueryDTO queryDTO);

    List<ConfigPO> listByCondition(ConfigPO condt);

    List<ConfigPO> listConfigByGroup(String groupName);

    List<String>  listDistinctGroup();

    ConfigPO getbyId(Integer configId);

    ConfigPO getByGroupAndName(String valueGroup, String valueName);
}
