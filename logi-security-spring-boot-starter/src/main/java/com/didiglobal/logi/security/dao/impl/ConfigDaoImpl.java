package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.constant.FieldConstant;
import com.didiglobal.logi.security.common.dto.config.ConfigQueryDTO;
import com.didiglobal.logi.security.common.po.ConfigPO;
import com.didiglobal.logi.security.dao.ConfigDao;
import com.didiglobal.logi.security.dao.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class ConfigDaoImpl extends BaseDaoImpl<ConfigPO> implements ConfigDao {

    private final static String ID              = "id";
    private final static String VALUE_GROUP     = "value_group";
    private final static String VALUE_NAME      = "value_name";
    private final static String VALUE           = "value";
    private final static String STATUS          = "status";
    private final static String MEMO            = "memo";
    private final static String OPERATOR        = "operator";
    private final static String CREATE_TIME     = "create_time";
    private final static String UPDATE_TIME     = "update_time";

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public int insert(ConfigPO param) {
        param.setAppName(logiSecurityProper.getAppName());
        return configMapper.insert(param);
    }

    @Override
    public int updateById(ConfigPO param) {
        return configMapper.updateById(param);
    }

    @Override
    public int update(ConfigPO param) {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq(VALUE_GROUP, param.getValueGroup());
        queryWrapper.eq(VALUE_NAME, param.getValueName());

        return configMapper.update(param, queryWrapper);
    }

    @Override
    public int deleteById(Integer id) {
        return configMapper.deleteById(id);
    }

    @Override
    public IPage<ConfigPO> selectPage(ConfigQueryDTO queryDTO){
        IPage<ConfigPO> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<ConfigPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper
                .eq( queryDTO.getValueGroup() != null, VALUE_GROUP, queryDTO.getValueGroup())
                .eq( queryDTO.getStatus() != null, STATUS, queryDTO.getStatus())
                .eq( queryDTO.getOperator() != null, OPERATOR, queryDTO.getOperator())
                .like(queryDTO.getMemo() != null, MEMO, queryDTO.getMemo())
                .like(queryDTO.getValueName() != null, VALUE_NAME, queryDTO.getValueName())
                .orderByDesc(FieldConstant.CREATE_TIME);
        configMapper.selectPage(page, queryWrapper);

        page.setTotal(configMapper.selectCount(queryWrapper));
        return page;
    }

    @Override
    public List<ConfigPO> listByCondition(ConfigPO condt) {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        if(!StringUtils.isBlank(condt.getValueGroup())){
            queryWrapper.eq(VALUE_GROUP, condt.getValueGroup());
        }

        if(!StringUtils.isBlank(condt.getValueName())){
            queryWrapper.eq(VALUE_NAME, condt.getValueName());
        }

        if(null != condt.getStatus()){
            queryWrapper.eq(STATUS, condt.getStatus());
        }

        return configMapper.selectList(queryWrapper);
    }

    @Override
    public List<ConfigPO> listConfigByGroup(String groupName) {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq(VALUE_GROUP, groupName);

        return configMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> listDistinctGroup() {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        queryWrapper.select( "distinct " + VALUE_GROUP);
        List<ConfigPO> configPOS = configMapper.selectList(queryWrapper);

        if(!CollectionUtils.isEmpty(configPOS)){
            return configPOS.stream().map(ConfigPO::getValueGroup).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public ConfigPO getbyId(Integer configId) {
        return configMapper.selectById(configId);
    }

    @Override
    public ConfigPO getByGroupAndName(String valueGroup, String valueName) {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq(VALUE_GROUP, valueGroup);
        queryWrapper.eq(VALUE_NAME, valueName);
        return configMapper.selectOne(queryWrapper);
    }

    private QueryWrapper<ConfigPO> wrapBriefQuery() {
        QueryWrapper<ConfigPO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select(ID, VALUE_GROUP, VALUE_NAME, VALUE, STATUS, MEMO, OPERATOR, CREATE_TIME, UPDATE_TIME);
        return queryWrapper;
    }
}
