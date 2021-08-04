package com.didiglobal.logi.job.mapper;

import com.didiglobal.logi.job.common.bean.AuvJob;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 正在执行的job信息 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvJobMapper {

  @Delete("delete from auv_job where code=#{code}")
  int deleteByCode(String code);

  @Insert("INSERT INTO auv_job(code, task_code, class_name, try_times, worker_code, start_time, "
          + "create_time, update_time, app_name) VALUES(#{code}, #{taskCode}, #{className}, #{tryTimes}, "
          + "#{workerCode}, #{startTime}, #{createTime}, #{updateTime}, #{appName})")
  int insert(AuvJob auvJob);

  @Select("select id, code, task_code, class_name, try_times, worker_code, start_time, create_time,"
          + " update_time, app_name from auv_job")
  List<AuvJob> selectAll();

  @Select("select id, code, task_code, class_name, try_times, worker_code, start_time, create_time,"
          + " update_time, app_name from auv_job where app_name=#{appName}")
  List<AuvJob> selectByAppName(@Param("appName") String appName);

}
