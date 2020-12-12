package com.didiglobal.logi.auvjob.core.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * task lock service.
 *
 * @author dengshan
 */
@Service
public class TaskLockServiceImpl implements TaskLockService {
  private static final Logger logger = LoggerFactory.getLogger(TaskLockServiceImpl.class);

  private AuvTaskLockMapper auvTaskLockMapper;

  /**
   * constructor.
   *
   * @param AuvTaskLockMapper task lock mapper
   */
  @Autowired
  public TaskLockServiceImpl(AuvTaskLockMapper auvTaskLockMapper) {
    this.auvTaskLockMapper = auvTaskLockMapper;
  }

  @Override
  public Boolean tryAcquire(String taskCode) {
    List<AuvTaskLock> auvTaskLockList = auvTaskLockMapper.selectList(new QueryWrapper<AuvTaskLock>()
            .eq("task_code", taskCode));
    if (CollectionUtils.isNotEmpty(auvTaskLockList)) {
      return false;
    }
    AuvTaskLock taskLock = new AuvTaskLock();
    taskLock.setTaskCode(taskCode);
    taskLock.setWorkerCode(WorkerSingleton.getInstance().getWorkerInfo().getCode());
    return auvTaskLockMapper.insert(taskLock) > 0 ? true : false;
  }

  @Override
  public Boolean tryRelease(String taskCode) {
    List<AuvTaskLock> auvTaskLockList = auvTaskLockMapper.selectList(new QueryWrapper<AuvTaskLock>()
            .eq("task_code", taskCode));
    if (CollectionUtils.isEmpty(auvTaskLockList)) {
      logger.error("class=TaskLockServiceImpl||method=tryRelease||url=||msg=not exists task "
              + "lock, taskCode [{}]", taskCode);
      return false;
    }
    int result = auvTaskLockMapper.delete(new QueryWrapper<AuvTaskLock>().eq("task_code", taskCode));
    return result > 0 ? true : false;
  }
}
