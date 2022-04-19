package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.config.ConfigDTO;
import com.didiglobal.logi.security.common.dto.config.ConfigQueryDTO;
import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.enums.ConfigStatusEnum;
import com.didiglobal.logi.security.common.po.ConfigPO;
import com.didiglobal.logi.security.common.vo.config.ConfigVO;
import com.didiglobal.logi.security.dao.ConfigDao;
import com.didiglobal.logi.security.service.ConfigService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author didi
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    private static final String NOT_EXIST   = "配置不存在";

    private static final String SYSTEM     = "system";

    @Autowired
    private ConfigDao configDao;

    private Cache<String, ConfigPO> configCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(100).build();

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

        configInfoPO.setOperator(user);
        configInfoPO.setIsDelete(true);
        return Result.build(1 == configDao.update(configInfoPO));
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
        if (null == configInfoDTO.getId()) {
            return Result.buildParamIllegal("配置ID为空");
        }

        ConfigPO configInfoPO = configDao.getbyId(configInfoDTO.getId());
        if (configInfoPO == null) {
            return Result.buildNotExist(NOT_EXIST);
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
        return Result.build(1 == configDao.update(configInfoPO));
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
     * 修改一个配置项的值
     * @param group 配置组
     * @param name  配置名字
     * @param value 配置内容
     * @return 成功 true  失败 false
     *
     * NotExistExceptio 配置不存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateValueByGroupAndName(String group, String name, String value) {
        if (value == null) {
            return Result.buildParamIllegal("值为空");
        }

        ConfigPO configInfoPO = getByGroupAndNameFromDB(group, name);
        if (configInfoPO == null) {
            return Result.buildNotExist(NOT_EXIST);
        }

        ConfigDTO param = new ConfigDTO();
        param.setId(configInfoPO.getId());
        param.setValue(value);

        return editConfig(param, SYSTEM);
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
            ConfigPO configInfoPO = getByGroupAndName(group, name);
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

    private ConfigPO getByGroupAndName(String group, String valueName) {
        try {
            return configCache.get(group + "@" + valueName, () -> getByGroupAndNameFromDB(group, valueName));
        } catch (Exception e) {
            return getByGroupAndNameFromDB(group, valueName);
        }
    }

    private ConfigPO getByGroupAndNameFromDB(String group, String valueName) {
        return configDao.getByGroupAndName(group, valueName);
    }
}
