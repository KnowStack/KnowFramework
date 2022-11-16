package com.didiglobal.knowframework.job.core.task;

import com.didiglobal.knowframework.job.common.Result;
import com.didiglobal.knowframework.job.common.domain.LogITask;
import com.didiglobal.knowframework.job.common.dto.LogITaskDTO;
import com.didiglobal.knowframework.job.common.dto.TaskPageQueryDTO;

import java.util.List;

/**
 * 任务的CRUD及执行管控.
 *
 * @author ds
 */
public interface TaskManager {

    /**
     * 更新任务.
     *
     * @param taskCode task taskCode
     * @return deleted auv task
     */
    Result delete(String taskCode);

    /**
     * 更新任务
     * @param logITaskDTO 更新信息
     * @return boolean
     */
    boolean update(LogITaskDTO logITaskDTO);

    /**
     * 接下来需要执行的任务,按时间先后顺序排序.
     *
     * @param interval 从现在开始下次执行时间间隔 毫秒
     * @return task info list
     */
    List<LogITask> nextTriggers(Long interval);

    /**
     * 接下来需要执行的任务,按时间先后顺序排序.
     *
     * @param fromTime fromTime
     * @param interval interval
     * @return task info list
     */
    List<LogITask> nextTriggers(Long fromTime, Long interval);

    /**
     * 提交任务，执行器会根据一致性协同算法判断是否执行.
     *
     * @param logITaskList task info list
     */
    void submit(List<LogITask> logITaskList);

    /**
     * 根据 task taskCode 执行任务.
     *
     * @param taskCode    task taskCode
     * @param executeSubs 是否执行子任务
     * @return Result
     */
    Result execute(String taskCode, Boolean executeSubs);

    /**
     * 执行任务, 默认会执行子任务如果有配置.
     *
     * @param logITask    任务信息
     * @param executeSubs 是否执行子任务
     */
    void execute(LogITask logITask, Boolean executeSubs);

    /**
     * 停止所有正在运行的job.
     *
     * @return stopByJobCode job size
     */
    int stopAll();

    /**
     * 更改某个任务的状态
     *
     * @param taskCode 任务编号
     * @param status 状态
     * @return 是否成功
     */
    Result<Boolean> updateTaskStatus(String taskCode, int status);

    /**
     * 获取所有任务.
     *
     * @return all tasks
     */
    List<LogITask> getAllRuning();

    /**
     * 获取所有任务个数
     * @param queryDTO 查询条件
     * @return 任务个数
     */
    int pagineTaskConut(TaskPageQueryDTO queryDTO);

    /**
     * 分页获取相关任务
     *
     * @param taskPageQueryDTO 任务分页查询信息
     * @return 任务List
     */
    List<LogITask> getPagineList(TaskPageQueryDTO taskPageQueryDTO);

    /**
     * 恢复任务 并释放锁.
     *
     * @param taskCode taskCode
     * @param workerCode 任务编号
     * @return true/false
     */
    Result<Boolean> release(String taskCode, String workerCode);

    /**
     * 获取某个具体的任务
     *
     * @param taskCode 任务编号
     * @return 任务信息
     */
    LogITask getByCode(String taskCode);

    /**
     * 添加任务
     * @param dto 待添加任务信息
     * @return 任务添加结果
     */
    Result add(LogITaskDTO dto);

}
