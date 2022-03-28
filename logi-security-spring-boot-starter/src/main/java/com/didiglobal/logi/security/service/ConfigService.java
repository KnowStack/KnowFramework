package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.config.ConfigDTO;
import com.didiglobal.logi.security.common.vo.config.ConfigVO;

import java.util.List;

public interface ConfigService {
    /**
     * 新增配置
     * @param configInfoDTO 配置信息
     * @param user 操作人
     * @return 成功 true
     */
    Result<Integer> addConfig(ConfigDTO configInfoDTO, String user);

    /**
     * 删除配置
     * @param configId 配置id
     * @param user 操作人
     * @return 成功 true  失败 false
     */
    Result<Void> delConfig(Integer configId, String user);

    /**
     * 编辑配置
     * @param configInfoDTO 配置内容
     * @param user 操作人
     * @return 成功 true  失败 false
     *
     */
    Result<Void> editConfig(ConfigDTO configInfoDTO, String user);

    /**
     * 使能配置
     * @param configId 配置id
     * @param status 状态
     * @param user 操作人
     * @return 成功 true  失败 false
     *
     */
    Result<Void> switchConfig(Integer configId, Integer status, String user);

    /**
     * 根据查询条件返回ConfigVOVO列表
     * @param param 查询条件
     * @return 配置列表
     *
     * 如果不存在,返回空列表
     */
    List<ConfigVO> queryByCondt(ConfigDTO param);

    /**
     * 获取本appName下的所有组名
     * @return
     */
    List<String> listGroups();

    /**
     * 根据组名，获取该组名下所有的配置
     * @param group
     * @return
     */
    List<ConfigVO> listConfigByGroup(String group);

    /**
     * 查询指定配置
     * @param configId 配置id
     * @return 配置信息  不存在返回null
     */
    ConfigVO getConfigById(Integer configId);

    /**
     * 修改一个配置项的值
     * @param group 配置组
     * @param name 配置名字
     * @param value 配置内容
     * @return  成功 true  失败 false
     *
     */
    Result<Void> updateValueByGroupAndName(String group, String name, String value);

    /**
     * 获取String类型配置
     * @param group 配置组
     * @param name 配置项
     * @param defaultValue 默认值
     * @return 如果查到转换后返回,转换报错或者没有查到则返回默认值
     */
    String stringSetting(String group, String name, String defaultValue);
}
