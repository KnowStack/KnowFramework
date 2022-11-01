package com.didiglobal.knowframework.security.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.didiglobal.knowframework.security.common.PagingData;
import com.didiglobal.knowframework.security.common.Result;
import com.didiglobal.knowframework.security.common.po.ConfigPO;
import com.didiglobal.knowframework.security.dao.ConfigDao;
import com.didiglobal.knowframework.security.service.ConfigService;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.didiglobal.knowframework.security.common.dto.config.ConfigDTO;
import com.didiglobal.knowframework.security.common.dto.config.ConfigQueryDTO;
import com.didiglobal.knowframework.security.common.enums.ConfigStatusEnum;
import com.didiglobal.knowframework.security.common.vo.config.ConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author didi
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {
    private static final Logger LOGGER  = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private static final String NOT_EXIST   = "配置不存在";

    private static final String SYSTEM     = "system";

    @Autowired
    private ConfigDao configDao;

    /**
     * 新增配置
     * @param configInfoDTO 配置信息
     * @param user      操作人
     * @return 成功 true
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> addConfig(ConfigDTO configInfoDTO, String user) {
        Result<Void> checkResult = checkParam(configInfoDTO);
        if (checkResult.failed()) {
            log.warn("class=ConfigVOServiceImpl||method=addConfig||msg={}||msg=check fail!",
                    checkResult.getMessage());
            return Result.buildFrom(checkResult);
        }

        configInfoDTO.setOperator(user);
        initConfig(configInfoDTO);

        ConfigPO oldConfig = getByGroupAndNameFromDB(configInfoDTO.getValueGroup(),
                configInfoDTO.getValueName());
        if (oldConfig != null) {
            return Result.buildDuplicate("配置重复");
        }

        ConfigPO param = CopyBeanUtil.copy(configInfoDTO, ConfigPO.class);
        configDao.insert(param);
        return Result.buildSucc(param.getId());
    }

    @Override
    public Result<Integer> addConfig(String valueGroup, String valueName, String value, String user) {
        ConfigDTO configInfoDTO = new ConfigDTO();
        configInfoDTO.setValueGroup( valueGroup );
        configInfoDTO.setValueName( valueName );
        configInfoDTO.setValue(value);
        configInfoDTO.setStatus(ConfigStatusEnum.NORMAL.getCode());

        return addConfig(configInfoDTO, user);
    }

    /**
     * 删除配置
     * @param configId 配置id
     * @param user 操作人
     * @return 成功 true  失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delConfig(Integer configId, String user) {
        ConfigPO configInfoPO = configDao.getbyId(configId);
        if (configInfoPO == null) {
            return Result.buildNotExist(NOT_EXIST);
        }

        return Result.build(1 == configDao.deleteById(configInfoPO.getId()));
    }

    /**
     * 编辑配置 只能编辑值  组和名称不能修改
     * @param configInfoDTO 配置内容
     * @param user      操作人
     * @return 成功 true  失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> editConfig(ConfigDTO configInfoDTO, String user) {
        if (StringUtils.isBlank(configInfoDTO.getValueGroup())
            || StringUtils.isBlank(configInfoDTO.getValueName())) {
            return Result.buildParamIllegal("配置组或者配置名为空");
        }

        if(ConfigStatusEnum.NORMAL.getCode() != configInfoDTO.getStatus().intValue()
            && ConfigStatusEnum.DISABLE.getCode() != configInfoDTO.getStatus().intValue()){
            return Result.buildParamIllegal("状态只能是1或者2");
        }

        ConfigPO configInfoPO = configDao.getByGroupAndName(configInfoDTO.getValueGroup(), configInfoDTO.getValueName());
        if (configInfoPO == null) {
            Result<Integer> retAdd = addConfig(configInfoDTO, user);
            return Result.buildFrom(retAdd);
        }

        configInfoPO.setOperator(user);
        boolean succ = (1 == configDao.update(CopyBeanUtil.copy(configInfoDTO, ConfigPO.class)));
        return Result.build(succ);
    }

    /**
     * 使能配置
     * @param configId 配置id
     * @param status   状态
     * @param user 操作人
     * @return 成功 true  失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> switchConfig(Integer configId, Integer status, String user) {
        ConfigPO configInfoPO = configDao.getbyId(configId);
        if (configInfoPO == null) {
            return Result.buildNotExist(NOT_EXIST);
        }

        ConfigStatusEnum statusEnum = ConfigStatusEnum.valueOf(status);
        if (statusEnum == null) {
            return Result.buildParamIllegal("状态非法");
        }

        configInfoPO.setOperator(user);
        configInfoPO.setStatus(status);
        return Result.build(1 == configDao.updateById(configInfoPO));
    }

    @Override
    public PagingData<ConfigVO> pagingConfig(ConfigQueryDTO queryDTO) {
        IPage<ConfigPO> configPOs = configDao.selectPage(queryDTO);
        List<ConfigVO> configVOS  = CopyBeanUtil.copyList(configPOs.getRecords(), ConfigVO.class);

        return new PagingData<>(configVOS, configPOs);
    }

    /**
     * 根据查询条件返回ConfigVOVO列表
     * @param param 查询条件
     * @return 配置列表
     *
     * 如果不存在,返回空列表
     */
    @Override
    public List<ConfigVO> queryByCondt(ConfigDTO param) {
        List<ConfigPO> configInfoPOs = configDao
                .listByCondition(CopyBeanUtil.copy(param, ConfigPO.class));
        return CopyBeanUtil.copyList(configInfoPOs, ConfigVO.class);
    }

    @Override
    public List<String> listGroups(){
        return configDao.listDistinctGroup();
    }

    @Override
    public List<ConfigVO> listConfigByGroup(String group) {
        List<ConfigPO> configInfoPOs = configDao
                .listConfigByGroup(group);

        return CopyBeanUtil.copyList(configInfoPOs, ConfigVO.class);
    }

    /**
     * 查询指定配置
     * @param configId 配置id
     * @return 配置信息  不存在返回null
     */
    @Override
    public ConfigVO getConfigById(Integer configId) {
        return CopyBeanUtil.copy(configDao.getbyId(configId), ConfigVO.class);
    }

    /**
     * 获取String类型配置
     * @param group        配置组
     * @param name         配置项
     * @param defaultValue 默认值
     * @return 如果查到转换后返回, 转换报错或者没有查到则返回默认值
     */
    @Override
    public String stringSetting(String group, String name, String defaultValue) {
        try {
            ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
            if (configInfoPO == null || StringUtils.isBlank(configInfoPO.getValue())) {
                return defaultValue;
            }
            return configInfoPO.getValue();
        } catch (Exception e) {
            log.warn("class=ConfigVOServiceImpl||method=stringSetting||group={}||name={}||msg=get config error!",
                    group, name, e);
        }
        return defaultValue;
    }

    /**
     * 获取int类型配置
     * @param group        配置组
     * @param name         配置项
     * @param defaultValue 默认值
     * @return 如果查到转换后返回, 转换报错或者没有查到则返回默认值
     */
    @Override
    public Integer intSetting(String group, String name, Integer defaultValue) {
        try {
            ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
            if (configInfoPO == null || StringUtils.isBlank(configInfoPO.getValue())) {
                return defaultValue;
            }
            return Integer.valueOf(configInfoPO.getValue());
        } catch (NumberFormatException e) {
            LOGGER.warn("class=ConfigServiceImpl||method=intSetting||group={}||name={}||msg=get config error!",
                    group, name);
        }
        return defaultValue;
    }

    /**
     * 获取long类型配置
     * @param group        配置组
     * @param name         配置项
     * @param defaultValue 默认值
     * @return 如果查到转换后返回, 转换报错或者没有查到则返回默认值
     */
    @Override
    public Long longSetting(String group, String name, Long defaultValue) {
        try {
            ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
            if (configInfoPO == null || StringUtils.isBlank(configInfoPO.getValue())) {
                return defaultValue;
            }
            return Long.valueOf(configInfoPO.getValue());
        } catch (Exception e) {
            LOGGER.warn("class=ConfigServiceImpl||method=longSetting||group={}||name={}||msg=get config error!",
                    group, name);
        }
        return defaultValue;
    }

    /**
     * 获取double类型配置
     * @param group        配置组
     * @param name         配置项
     * @param defaultValue 默认值
     * @return 如果查到转换后返回, 转换报错或者没有查到则返回默认值
     */
    @Override
    public Double doubleSetting(String group, String name, Double defaultValue) {
        try {
            ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
            if (configInfoPO == null || StringUtils.isBlank(configInfoPO.getValue())) {
                return defaultValue;
            }
            return Double.valueOf(configInfoPO.getValue());
        } catch (Exception e) {
            LOGGER.warn( "class=ConfigServiceImpl||method=doubleSetting||group={}||name={}||msg=get config error!",
                    group, name, e);
        }
        return defaultValue;
    }

    /**
     * 获取bool类型配置
     * @param group        配置组
     * @param name         配置项
     * @param defaultValue 默认值
     * @return 如果查到转换后返回, 转换报错或者没有查到则返回默认值
     */
    @Override
    public Boolean booleanSetting(String group, String name, Boolean defaultValue) {
        ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
        if (configInfoPO == null || StringUtils.isBlank(configInfoPO.getValue())) {
            return defaultValue;
        }
        return Boolean.valueOf(configInfoPO.getValue());
    }

    /**
     * 获取Object类型配置
     * @param group        配置组
     * @param name         配置项
     * @param defaultValue 默认值
     * @param clazz        返回类型
     * @return 如果查到转换后返回, 转换报错或者没有查到则返回默认值
     */
    @Override
    public <T> T objectSetting(String group, String name, T defaultValue, Class<T> clazz) {
        try {
            ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
            if (configInfoPO == null || StringUtils.isBlank(configInfoPO.getValue())) {
                return defaultValue;
            }
            return JSON.parseObject(configInfoPO.getValue(), clazz);
        } catch (Exception e) {
            LOGGER.warn( "class=ConfigServiceImpl||method=objectSetting||group={}||name={}||msg=get config error!",
                    group, name, e);
        }
        return defaultValue;
    }

    /******************************************* private method **************************************************/
    private Result<Void> checkParam(ConfigDTO configInfoDTO) {
        if (null == configInfoDTO) {
            return Result.buildParamIllegal("配置信息为空");
        }
        if (StringUtils.isBlank(configInfoDTO.getValueGroup())) {
            return Result.buildParamIllegal("组为空");
        }
        if (StringUtils.isBlank(configInfoDTO.getValueName())) {
            return Result.buildParamIllegal("名字为空");
        }
        return Result.buildSucc(null);
    }

    private void initConfig(ConfigDTO configInfoDTO) {
        if (configInfoDTO.getStatus() == null) {
            configInfoDTO.setStatus(ConfigStatusEnum.NORMAL.getCode());
        }

        if (configInfoDTO.getValue() == null) {
            configInfoDTO.setValue("");
        }

        if (configInfoDTO.getMemo() == null) {
            configInfoDTO.setMemo("");
        }
    }

    private ConfigPO getByGroupAndNameFromDB(String group, String valueName) {
        return configDao.getByGroupAndName(group, valueName);
    }
}
