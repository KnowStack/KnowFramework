package com.didiglobal.logi.auvjob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.auvjob.common.bean.AuvWorker;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * worker信息 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvWorkerMapper extends BaseMapper<AuvWorker> {

  @Update("INSERT INTO auv_worker(code, name, cpu, cpu_used, memory, memory_used, jvm_memory, "
          + "jvm_memory_used, job_num, heartbeat) VALUES(#{code}, #{name}, #{cpu}, #{cpuUsed}, "
          + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{jobNum}, #{heartbeat}) "
          + "ON DUPLICATE KEY UPDATE cpu=#{cpu}, name=#{name}, cpu_used=#{cpuUsed}, "
          + "memory=#{memory}, memory_used=#{memoryUsed}, jvm_memory=#{jvmMemory}, "
          + "jvm_memory_used=#{jvmMemoryUsed}, job_num=#{jobNum}, heartbeat=#{heartbeat}")
  int saveOrUpdateById(AuvWorker auvWorker);
}
