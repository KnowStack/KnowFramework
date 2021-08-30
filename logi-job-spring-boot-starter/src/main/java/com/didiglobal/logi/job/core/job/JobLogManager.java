package com.didiglobal.logi.job.core.job;

import com.didiglobal.logi.job.common.vo.LogIJobLogVO;

import java.util.List;

public interface JobLogManager {

    /**
     *
     * @param taskCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<LogIJobLogVO> getJobLogs(String taskCode, int pageNo, int pageSize);

    /**
     *
     * @param taskCode
     * @return
     */
    int getJobLogsCount(String taskCode);
}
