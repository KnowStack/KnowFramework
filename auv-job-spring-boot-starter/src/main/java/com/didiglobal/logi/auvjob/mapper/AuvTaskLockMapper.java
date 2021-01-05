package com.didiglobal.logi.auvjob.mapper;

import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 任务锁 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvTaskLockMapper {

  @Insert("INSERT INTO auv_task_lock(task_code, worker_code) VALUES(#{taskCode}, #{workerCode})")
  int insert(AuvTaskLock auvTaskLock);

  @Select("select id, task_code, worker_code, create_time, update_time from auv_task_lock where "
          + "task_code=#{taskCode}")
  List<AuvTaskLock> selectByTaskCode(@Param("taskCode") String taskCode);

  @Delete("delete from auv_task_lock where task_code=#{taskCode}")
  int deleteByTaskCode(@Param("taskCode") String taskCode);

  @Delete("delete from auv_task_lock where worker_code=#{workerCode}")
  int deleteByWorkerCode(@Param("workerCode") String workerCode);

}
