package com.didiglobal.knowframework.job.core.job.impl;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.job.core.job.JobLogManager;
import com.didiglobal.knowframework.job.core.task.TaskManager;
import com.didiglobal.knowframework.job.utils.BeanUtil;
import com.didiglobal.knowframework.job.LogIJobProperties;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.job.common.domain.LogITask;
import com.didiglobal.knowframework.job.common.dto.TaskLogPageQueryDTO;
import com.didiglobal.knowframework.job.common.po.LogIJobLogPO;
import com.didiglobal.knowframework.job.common.vo.LogIJobLogVO;
import com.didiglobal.knowframework.job.extend.JobLogFetcherExtendBeanTool;
import com.didiglobal.knowframework.job.mapper.LogIJobLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobLogManagerImpl implements JobLogManager {

    private TaskManager taskManager;
    private LogIJobLogMapper logIJobLogMapper;
    private LogIJobProperties logIJobProperties;

    private final static String SORT_DESC   = "desc";
    private final static String SORT_ASC    = "asc";

    private final static String SORT_ID             = "id";
    private final static String SORT_STATUS         = "status";
    private final static String SORT_RESULT         = "result";
    private final static String SORT_CREATE_TIME    = "create_time";
    private final static String SORT_START_TIME     = "start_time";
    private final static String SORT_END_TIME       = "end_time";

    @Autowired
    private JobLogFetcherExtendBeanTool jobLogFetcherExtendBeanTool;

    @Autowired
    public JobLogManagerImpl(TaskManager taskManager,
                             LogIJobLogMapper logIJobLogMapper,
                             LogIJobProperties logIJobProperties) {
        this.taskManager = taskManager;
        this.logIJobLogMapper = logIJobLogMapper;
        this.logIJobProperties = logIJobProperties;
    }

    @Override
    public List<LogIJobLogVO> pageJobLogs(TaskLogPageQueryDTO dto) {
        Map<Long, LogITask> longLogITaskMap = new HashMap<>();

        Timestamp beginTimestamp = null;
        Timestamp endTimestamp = null;

        if (null != dto.getBeginTime()) {
            beginTimestamp = new Timestamp(dto.getBeginTime());
        }

        if (null != dto.getEndTime()) {
            endTimestamp = new Timestamp(dto.getEndTime());
        }

        List<LogIJobLogPO> logIJobLogPOS = logIJobLogMapper.pagineListByCondition(logIJobProperties.getAppName(),
                dto.getTaskId(), dto.getTaskDesc(), dto.getTaskStatus(),
                (dto.getPage() - 1) * dto.getSize(), dto.getSize(),
                genSortName(dto.getSortName()) , genSort(dto.getSortAsc()),
                beginTimestamp, endTimestamp);

        if (CollectionUtils.isEmpty(logIJobLogPOS)) {
            return null;
        }

        return logIJobLogPOS.stream().map(logIJobLogPO -> {
            LogIJobLogVO logIJobLogVO = BeanUtil.convertTo(logIJobLogPO, LogIJobLogVO.class);

            LogITask logITask = longLogITaskMap.get(logIJobLogPO.getTaskId());
            if (null == logITask) {
                logITask = taskManager.getByCode(logIJobLogPO.getTaskCode());
                longLogITaskMap.put(logIJobLogPO.getTaskId(), logITask);
            }

            List<String> ips = logITask.getTaskWorkers().stream().map(w -> w.getIp()).collect(Collectors.toList());
            logIJobLogVO.setAllWorkerIps(ips);

            logIJobLogVO.setTaskName(logITask.getTaskName());
            return logIJobLogVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getJobLogsCount(TaskLogPageQueryDTO dto) {

        Timestamp beginTimestamp = null;
        Timestamp endTimestamp = null;

        if (null != dto.getBeginTime()) {
            beginTimestamp = new Timestamp(dto.getBeginTime());
        }

        if (null != dto.getEndTime()) {
            endTimestamp = new Timestamp(dto.getEndTime());
        }

        return logIJobLogMapper.pagineCountByCondition(logIJobProperties.getAppName(),
                dto.getTaskId(), dto.getTaskDesc(), dto.getTaskStatus(),
                beginTimestamp, endTimestamp);
    }

    private TaskResult getJobLogResult(Long id) {
        LogIJobLogPO logIJobLogPO = logIJobLogMapper.selectById(id);
        if(StringUtils.isNotBlank(logIJobLogPO.getResult())) {
            return JSON.parseObject(logIJobLogPO.getResult(), TaskResult.class);
        } else {
            return null;
        }
    }

    @Override
    public List<String> getJobLog(Long id) throws Exception {
        TaskResult taskResult = getJobLogResult(id);
        if(null != taskResult) {
            String traceId = taskResult.getTraceId();
            if(StringUtils.isNotBlank(traceId)) {
                List<String> logs = jobLogFetcherExtendBeanTool.getLoginExtendImpl().getLogsByTraceIdFromExternalSystem(traceId);
                return logs;
            } else {
                throw new Exception(
                        String.format(
                                "JobLog id:%d 任务执行结果缺失 traceId，请确认是否正确配置可观测性组件",
                                id
                        )
                );
            }
        } else {
            throw new Exception(
                    String.format(
                            "JobLog id:%d 在系统中不存在任务执行结果",
                            id
                    )
            );
        }
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
