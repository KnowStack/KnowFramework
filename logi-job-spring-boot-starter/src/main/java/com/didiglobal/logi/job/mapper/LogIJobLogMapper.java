package com.didiglobal.logi.job.mapper;

import com.didiglobal.logi.job.common.po.LogIJobLogPO;
import java.sql.Timestamp;
import java.util.List;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * job执行历史日志 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface LogIJobLogMapper {

  @Insert("INSERT INTO logi_job_log(job_code, task_code, class_name, try_times, worker_code, worker_ip"
          + "start_time, end_time, status, error, result, create_time, update_time, app_name) "
          + "VALUES(#{jobCode}, #{taskCode}, #{className}, #{tryTimes}, #{workerCode}, #{startTime}"
          + ", #{endTime}, #{status}, #{error}, #{result}, #{createTime}, #{updateTime}, #{appName})")
  int insert(LogIJobLogPO logIJobLogPO);

  @Select("select id, job_code, task_code, class_name, try_times, worker_code, worker_ip, start_time, "
          + "end_time, status, error, result, create_time, update_time, app_name from logi_job_log where "
          + "task_code=#{taskCode} and app_name=#{appName} order by id desc limit #{start}, #{size}")
  List<LogIJobLogPO> selectByTaskCode(@Param("taskCode") String taskCode,
                                      @Param("appName") String appName,
                                      @Param("start") Integer start,
                                      @Param("size") Integer size);

  @Delete("delete from logi_job_log where create_time<=#{createTime} and app_name=#{appName}")
  int deleteByCreateTime(@Param("createTime") Timestamp createTime, @Param("appName") String appName);

  @Select("select count(1) from logi_task where app_name=#{appName} and task_code=#{taskCode}")
  int selectCountByAppName(@Param("appName") String appName, @Param("taskCode") String taskCode);
}
