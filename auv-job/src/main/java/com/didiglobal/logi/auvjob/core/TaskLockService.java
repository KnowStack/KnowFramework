package com.didiglobal.logi.auvjob.core;

/**
 * @author dengshan
 */
public interface TaskLockService {
  /**
   * 尝试获取锁
   * @return
   */
  Boolean tryAcquire();

  /**
   * 尝试释放锁
   * @return
   */
  Boolean tryRelease();
}
