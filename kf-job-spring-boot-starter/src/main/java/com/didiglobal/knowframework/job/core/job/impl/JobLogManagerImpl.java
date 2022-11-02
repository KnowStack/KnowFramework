package com.didiglobal.knowframework.job.core.job.impl;

import com.didiglobal.knowframework.job.KfJobProperties;
import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.common.dto.KfTaskLogPageQueryDTO;
import com.didiglobal.knowframework.job.common.po.KfJobLogPO;
import com.didiglobal.knowframework.job.common.vo.KfJobLogVO;
import com.didiglobal.knowframework.job.core.job.JobLogManager;
import com.didiglobal.knowframework.job.core.task.TaskManager;
import com.didiglobal.knowframework.job.mapper.KfJobLogMapper;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author didi
 */
@Service
public class JobLogManagerImpl implements JobLogManager {

    private TaskManager taskManager;
    private KfJobLogMapper kfJobLogMapper;
    private KfJobProperties kfJobProperties;

    private final static String SORT_DESC   = "desc";
    private final static String SORT_ASC    = "asc";

    private final static String SORT_ID             = "id";
    private final static String SORT_STATUS         = "status";
    private final static String SORT_RESULT         = "result";
    private final static String SORT_CREATE_TIME    = "create_time";
    private final static String SORT_START_TIME     = "start_time";
    private final static String SORT_END_TIME       = "end_time";

    @Autowired
    public JobLogManagerImpl(TaskManager taskManager,
                             KfJobLogMapper kfJobLogMapper,
                             KfJobProperties kfJobProperties) {
        this.taskManager = taskManager;
        this.kfJobLogMapper = kfJobLogMapper;
        this.kfJobProperties = kfJobProperties;
    }

    @Override
    public List<KfJobLogVO> pageJobLogs(KfTaskLogPageQueryDTO dto) {
        Map<Long, KfTask> longKfTaskMap = new HashMap<>();

        Timestamp beginTimestamp = null;
        Timestamp endTimestamp = null;

        if (null != dto.getBeginTime()) {
            beginTimestamp = new Timestamp(dto.getBeginTime());
        }

        if (null != dto.getEndTime()) {
            endTimestamp = new Timestamp(dto.getEndTime());
        }

        List<KfJobLogPO> kfJobLogPOS = kfJobLogMapper.pagineListByCondition( kfJobProperties.getAppName(),
                dto.getTaskId(), dto.getTaskDesc(), dto.getTaskStatus(),
                (dto.getPage() - 1) * dto.getSize(), dto.getSize(),
                genSortName(dto.getSortName()) , genSort(dto.getSortAsc()),
                beginTimestamp, endTimestamp);

        if (CollectionUtils.isEmpty( kfJobLogPOS )) {
            return null;
        }

        return kfJobLogPOS.stream().map( kfJobLogPO -> {
            KfJobLogVO kfJobLogVO = BeanUtil.convertTo( kfJobLogPO, KfJobLogVO.class);

            KfTask kfTask = longKfTaskMap.get( kfJobLogPO.getTaskId());
            if (null == kfTask) {
                kfTask = taskManager.getByCode( kfJobLogPO.getTaskCode());
                longKfTaskMap.put( kfJobLogPO.getTaskId(), kfTask );
            }

            List<String> ips = kfTask.getTaskWorkers().stream().map( w -> w.getIp()).collect(Collectors.toList());
            kfJobLogVO.setAllWorkerIps(ips);

            kfJobLogVO.setTaskName( kfTask.getTaskName());
            return kfJobLogVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getJobLogsCount(KfTaskLogPageQueryDTO dto) {

        Timestamp beginTimestamp = null;
        Timestamp endTimestamp = null;

        if (null != dto.getBeginTime()) {
            beginTimestamp = new Timestamp(dto.getBeginTime());
        }

        if (null != dto.getEndTime()) {
            endTimestamp = new Timestamp(dto.getEndTime());
        }

        return kfJobLogMapper.pagineCountByCondition( kfJobProperties.getAppName(),
                dto.getTaskId(), dto.getTaskDesc(), dto.getTaskStatus(),
                beginTimestamp, endTimestamp);
    }

    private String genSortName(String sortName){
        if(SORT_STATUS.equals(sortName)){return SORT_STATUS;}
        if(SORT_RESULT.equals(sortName)){return SORT_RESULT;}
        if(SORT_CREATE_TIME.equals(sortName)){return SORT_CREATE_TIME;}
        if(SORT_START_TIME.equals(sortName)){return SORT_START_TIME;}
        if(SORT_END_TIME.equals(sortName)){return SORT_END_TIME;}

        return SORT_ID;
    }

    private String genSort(String sortAsc){
        if(SORT_DESC.equals(sortAsc)){return SORT_DESC;}

        return SORT_ASC;
    }
}
