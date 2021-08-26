package com.didiglobal.logi.job.mapper;

import com.didiglobal.logi.job.common.bean.AuvJob;
import com.didiglobal.logi.job.common.bean.AuvWorker;
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
public interface AuvWorkerMapper {

  @Insert("INSERT INTO auv_worker(code, name, cpu, cpu_used, memory, memory_used, jvm_memory, "
          + "jvm_memory_used, job_num, heartbeat, app_name) VALUES(#{code}, #{name}, #{cpu}, #{cpuUsed}, "
          + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{job_num}, #{heartbeat}, #{appName})")
  int insert(AuvWorker auvWorker);

  @Update("INSERT INTO auv_worker(code, name, cpu, cpu_used, memory, memory_used, jvm_memory, "
          + "jvm_memory_used, job_num, heartbeat, app_name) VALUES(#{code}, #{name}, #{cpu}, #{cpuUsed}, "
          + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{jobNum}, #{heartbeat}, #{appName}) "
          + "ON DUPLICATE KEY UPDATE cpu=#{cpu}, name=#{name}, cpu_used=#{cpuUsed}, "
          + "memory=#{memory}, memory_used=#{memoryUsed}, jvm_memory=#{jvmMemory}, "
          + "jvm_memory_used=#{jvmMemoryUsed}, job_num=#{jobNum}, heartbeat=#{heartbeat}, app_name=#{appName}")
  int saveOrUpdateById(AuvWorker auvWorker);

  @Delete("delete from auv_worker where code=#{code}")
  int deleteByCode(@Param("code") String code);

  @Select("select code, name, cpu, cpu_used, memory, memory_used, jvm_memory,"
          + "jvm_memory_used, job_num, heartbeat, app_name, update_time from auv_worker where app_name=#{appName}")
  List<AuvWorker> selectByAppName(@Param("appName") String appName);
}
