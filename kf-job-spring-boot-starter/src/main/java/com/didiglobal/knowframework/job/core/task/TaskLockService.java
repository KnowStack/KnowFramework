package com.didiglobal.knowframework.job.core.task;

import com.didiglobal.knowframework.job.common.vo.LogITaskLockVO;

import java.util.List;

/**
 * task lock service.
 *
 * @author ds
 */
public interface TaskLockService {
    /**
     * 尝试获取锁.
     * @param taskCode 任务编号
     * @return true/false
     */
    Boolean tryAcquire(String taskCode);

    /**
     * 尝试获取锁.
     *
     * @param taskCode   taskCode
     * @param workerCode workerCode
     * @param expireTime expireTime
     * @return true/false
     */
    Boolean tryAcquire(String taskCode, String workerCode, Long expireTime);

    /**
     * 尝试释放锁.
     * @param taskCode 任务编号
     * @return true/false
     */
    Boolean tryRelease(String taskCode);

    /**
     * 尝试释放锁.
     *
     * @param taskCode   taskCode
     * @param workerCode workerCode
     * @return true/false
     */
    Boolean tryRelease(String taskCode, String workerCode);

    /**
     * 获取所有任务.
     *
     * @return tasks
     */
    List<LogITaskLockVO> getAll();

    /**
     * 刷新当前任务的锁.
     */
    void renewAll();
}
