package com.didiglobal.logi.job.core.job.impl;

import com.didiglobal.logi.job.LogIJobProperties;
import com.didiglobal.logi.job.common.domain.LogITask;
import com.didiglobal.logi.job.common.po.LogIJobLogPO;
import com.didiglobal.logi.job.common.vo.LogIJobLogVO;
import com.didiglobal.logi.job.core.job.JobLogManager;
import com.didiglobal.logi.job.core.task.TaskManager;
import com.didiglobal.logi.job.mapper.LogIJobLogMapper;
import com.didiglobal.logi.job.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobLogManagerImpl implements JobLogManager {

    private TaskManager taskManager;
    private LogIJobLogMapper logIJobLogMapper;
    private LogIJobProperties logIJobProperties;


    @Autowired
    public void JobLogManagerImpl(TaskManager taskManager,
                                  LogIJobLogMapper logIJobLogMapper,
                                  LogIJobProperties logIJobProperties){
        this.taskManager        = taskManager;
        this.logIJobLogMapper   = logIJobLogMapper;
        this.logIJobProperties  = logIJobProperties;
    }

    @Override
    public List<LogIJobLogVO> getJobLogs(String taskCode, int pageNo, int pageSize) {
        LogITask     task = taskManager.getByCode(taskCode);
        List<String> ips  = task.getTaskWorkers().stream().map(w -> w.getIp()).collect( Collectors.toList());

        List<LogIJobLogPO> logIJobLogPOS = logIJobLogMapper.selectByTaskCode(taskCode, logIJobProperties.getAppName(),
                (pageNo - 1) * pageSize, pageSize);

        if (CollectionUtils.isEmpty(logIJobLogPOS)) {
            return null;
        }
        return logIJobLogPOS.stream().map(logIJobLogPO -> {
            LogIJobLogVO logIJobLogVO = BeanUtil.convertTo(logIJobLogPO, LogIJobLogVO.class);
            logIJobLogVO.setTaskName(task.getTaskName());
            logIJobLogVO.setAllWorkerIps(ips);
            return logIJobLogVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getJobLogsCount(String taskCode) {
        return logIJobLogMapper.selectCountByAppName(logIJobProperties.getAppName(), taskCode);
    }
}
