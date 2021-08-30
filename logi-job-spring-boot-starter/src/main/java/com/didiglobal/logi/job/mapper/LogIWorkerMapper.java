package com.didiglobal.logi.job.mapper;

import com.didiglobal.logi.job.common.po.LogIWorkerPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * worker信息 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface LogIWorkerMapper {

  @Insert("INSERT INTO logi_worker(code, name, ip, cpu, cpu_used, memory, memory_used, jvm_memory, "
          + "jvm_memory_used, job_num, heartbeat, app_name) VALUES(#{code}, #{name}, #{cpu}, #{cpuUsed}, "
          + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{job_num}, #{heartbeat}, #{appName})")
  int insert(LogIWorkerPO logIWorkerPO);

  @Update("INSERT INTO logi_worker(code, name, ip, cpu, cpu_used, memory, memory_used, jvm_memory, "
          + "jvm_memory_used, job_num, heartbeat, app_name) VALUES(#{code}, #{name}, #{cpu}, #{cpuUsed}, "
          + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{jobNum}, #{heartbeat}, #{appName}) "
          + "ON DUPLICATE KEY UPDATE cpu=#{cpu}, name=#{name}, cpu_used=#{cpuUsed}, "
          + "memory=#{memory}, memory_used=#{memoryUsed}, jvm_memory=#{jvmMemory}, "
          + "jvm_memory_used=#{jvmMemoryUsed}, job_num=#{jobNum}, heartbeat=#{heartbeat}, app_name=#{appName}")
  int saveOrUpdateById(LogIWorkerPO logIWorkerPO);

  @Delete("delete from logi_worker where code=#{code}")
  int deleteByCode(@Param("code") String code);

  @Select("select code, name, ip, cpu, cpu_used, memory, memory_used, jvm_memory,"
          + "jvm_memory_used, job_num, heartbeat, app_name, update_time from logi_worker where app_name=#{appName}")
  List<LogIWorkerPO> selectByAppName(@Param("appName") String appName);
}
