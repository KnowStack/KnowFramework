package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.common.dto.KfTaskLogPageQueryDTO;
import com.didiglobal.knowframework.job.common.vo.KfJobLogVO;

import java.util.List;

public interface JobLogManager {

    /**
     * @param pageQueryDTO 分页查询条件
     * @return 查询信息
     */
    List<KfJobLogVO> pageJobLogs(KfTaskLogPageQueryDTO pageQueryDTO);

    /**
     * @param pageQueryDTO 分页查询条件
     * @return 数量
     */
    int getJobLogsCount(KfTaskLogPageQueryDTO pageQueryDTO);
}
