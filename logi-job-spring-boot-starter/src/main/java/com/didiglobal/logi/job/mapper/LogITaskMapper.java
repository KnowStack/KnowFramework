package com.didiglobal.logi.job.mapper;

import com.didiglobal.logi.job.common.po.LogITaskPO;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 任务信息 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface LogITaskMapper {

  @Insert("INSERT INTO logi_task(code, name, description, cron, class_name, params, retry_times,"
          + " last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str, app_name, owner) "
          + "VALUES(#{code}, #{name}, #{description}, #{cron}, #{className}, #{params}, "
          + "#{retryTimes}, #{lastFireTime}, #{timeout}, #{status}, #{subTaskCodes}, "
          + "#{consensual}, #{taskWorkerStr}, #{appName}, #{owner})")
  int insert(LogITaskPO logITaskPO);

  @Delete("delete from logi_task where code=#{code} and app_name=#{appName}")
  int deleteByCode(@Param("code") String code, @Param("appName") String appName);

  @Update("update logi_task set name=#{name}, description=#{description}, cron=#{cron}, class_name="
          + "#{className}, params=#{params}, retry_times=#{retryTimes}, last_fire_time="
          + "#{lastFireTime}, timeout=#{timeout}, status=#{status}, sub_task_codes=#{subTaskCodes},"
          + " consensual=#{consensual}, task_worker_str=#{taskWorkerStr}, owner=#{owner} where code=#{code}")
  int updateByCode(LogITaskPO logITaskPO);

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, create_time, update_time "
          + ", task_worker_str, create_time, update_time, app_name, owner from logi_task where code=#{code} and app_name=#{appName}")
  LogITaskPO selectByCode(@Param("code") String code, @Param("appName") String appName);

  @Select("<script>"
          + "select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str, "
          + "create_time, update_time, app_name, owner from logi_task where app_name=#{appName} and codes in "
          + "<foreach collection='codes' item='code' index='index' open='(' close=')' "
          + "separator=','>"
          + "  #{code} "
          + "</foreach> "
          + "</script>")
  List<LogITaskPO> selectByCodes(@Param("codes") List<String> codes, @Param("appName") String appName);

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str,"
          + " create_time, update_time, app_name, owner from logi_task")
  List<LogITaskPO> selectAll();

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str,"
          + " create_time, update_time, app_name, owner from logi_task where app_name=#{appName}")
  List<LogITaskPO> selectByAppName(@Param("appName") String appName);

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str,"
          + " create_time, update_time, app_name, owner from logi_task where app_name=#{appName} and status=1")
  List<LogITaskPO> selectRuningByAppName(@Param("appName") String appName);

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str,"
          + " create_time, update_time, app_name, owner from logi_task where app_name=#{appName} "
          + " order by id desc limit #{start}, #{size} ")
  List<LogITaskPO> selectByAppNameAndSize(@Param("appName") String appName, @Param("start") int start, @Param("size") int size);

  @Select("select count(1) from logi_task where app_name=#{appName}")
  int selectCountByAppName(@Param("appName") String appName);
}
