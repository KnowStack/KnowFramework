package com.didiglobal.logi.auvjob.core.task;

/**
 * task lock service.
 *
 * @author dengshan
 */
public interface TaskLockService {
  /**
   * 尝试获取锁.
   *
   * @return true/false
   */
  Boolean tryAcquire(String taskCode);

  /**
   * 尝试释放锁.
   *
   * @return true/false
   */
  Boolean tryRelease(String taskCode);
}
