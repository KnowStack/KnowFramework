package com.didiglobal.logi.security.dao;

import com.didiglobal.logi.security.common.po.ConfigPO;

import java.util.List;

/**
 * @author cjm
 */
public interface ConfigDao {

    int insert(ConfigPO param);

    int update(ConfigPO param);

    List<ConfigPO> listByCondition(ConfigPO condt);

    List<ConfigPO> listConfigByGroup(String groupName);

    List<String>  listDistinctGroup();

    ConfigPO getbyId(Integer configId);

    ConfigPO getByGroupAndName(String valueGroup, String valueName);
}