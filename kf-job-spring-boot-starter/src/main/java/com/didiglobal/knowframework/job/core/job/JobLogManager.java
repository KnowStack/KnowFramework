package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.dto.TaskLogPageQueryDTO;
import com.didiglobal.knowframework.job.common.vo.LogIJobLogVO;

import java.util.List;

public interface JobLogManager {

    /**
     * @param pageQueryDTO 分页查询条件
     * @return 查询信息
     */
    List<LogIJobLogVO> pageJobLogs(TaskLogPageQueryDTO pageQueryDTO);

    /**
     * @param pageQueryDTO 分页查询条件
     * @return 数量
     */
    int getJobLogsCount(TaskLogPageQueryDTO pageQueryDTO);

    /**
     * @param id job log id
     * @return job log 上下文相关日志信息
     */
    List<String> getJobLog(Long id) throws Exception;

}
