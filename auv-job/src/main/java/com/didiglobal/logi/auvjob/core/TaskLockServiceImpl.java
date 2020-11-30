package com.didiglobal.logi.auvjob.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * task lock service.
 *
 * @author dengshan
 */
public class TaskLockServiceImpl implements TaskLockService {
  private static final Logger logger = LoggerFactory.getLogger(TaskLockServiceImpl.class);

  private SqlSession sqlSession;
  private WorkerInfo workerInfo;

  private AuvTaskLockMapper auvTaskLockMapper;

  /**
   * constructor.
   *
   * @param sqlSession sqlSession
   * @param workerInfo worker info
   */
  public TaskLockServiceImpl(SqlSession sqlSession, WorkerInfo workerInfo) {
    this.sqlSession = sqlSession;
    this.workerInfo = workerInfo;

    this.auvTaskLockMapper = sqlSession.getMapper(AuvTaskLockMapper.class);
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
    taskLock.setWorkerCode(workerInfo.getCode());
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
    int result = auvTaskLockMapper.delete(new QueryWrapper<AuvTaskLock>().eq("taskCode", taskCode));
    return result > 0 ? true : false;
  }
}
