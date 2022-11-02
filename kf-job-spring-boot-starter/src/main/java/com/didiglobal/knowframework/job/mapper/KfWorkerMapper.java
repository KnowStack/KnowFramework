package com.didiglobal.knowframework.job.mapper;

import com.didiglobal.knowframework.job.common.po.KfWorkerPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * worker信息 Mapper 接口.
 * </p>
 *
 * @author ds
 * @since 2020-11-10
 */
@Mapper
public interface KfWorkerMapper {

    @Insert("INSERT INTO kf_worker(worker_code, worker_name, ip, cpu, cpu_used, memory, memory_used, jvm_memory, "
            + "jvm_memory_used, job_num, heartbeat, app_name) "
            + "VALUES(#{workerCode}, #{workerName}, #{ip}, #{cpu}, #{cpuUsed}, "
            + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{jobNum}, #{heartbeat}, #{appName})")
    int insert(KfWorkerPO kfWorkerPO);

    @Update("INSERT INTO kf_worker(worker_code, worker_name, ip, cpu, cpu_used, memory, memory_used, jvm_memory, "
            + "jvm_memory_used, job_num, heartbeat, app_name) "
            + "VALUES(#{workerCode}, #{workerName}, #{ip}, #{cpu}, #{cpuUsed}, "
            + "#{memory}, #{memoryUsed}, #{jvmMemory}, #{jvmMemoryUsed}, #{jobNum}, #{heartbeat}, #{appName}) "
            + "ON DUPLICATE KEY UPDATE cpu=#{cpu}, worker_name=#{workerName}, ip=#{ip}, cpu_used=#{cpuUsed}, "
            + "memory=#{memory}, memory_used=#{memoryUsed}, jvm_memory=#{jvmMemory}, "
            + "jvm_memory_used=#{jvmMemoryUsed}, job_num=#{jobNum}, heartbeat=#{heartbeat}, app_name=#{appName}")
    int saveOrUpdateById(KfWorkerPO kfWorkerPO);

    @Update("update kf_worker set cpu=#{cpu}, worker_name=#{workerName}, ip=#{ip}, cpu_used=#{cpuUsed}, "
            + "memory=#{memory}, memory_used=#{memoryUsed}, jvm_memory=#{jvmMemory}, "
            + "jvm_memory_used=#{jvmMemoryUsed}, job_num=#{jobNum}, heartbeat=#{heartbeat}, app_name=#{appName} where worker_code=#{workerCode} and app_name=#{appName}")
    int updateByCode(KfWorkerPO kfWorkerPO);

    @Select("select id, worker_code, worker_name, ip, cpu, cpu_used, memory, memory_used, jvm_memory, jvm_memory_used, job_num, heartbeat, app_name "
            + "from kf_worker where worker_code=#{workerCode} and app_name=#{appName}")
    KfWorkerPO selectByCode(@Param("workerCode") String code, @Param("appName") String appName);

    @Delete("delete from kf_worker where worker_code=#{workerCode}")
    int deleteByCode(@Param("workerCode") String code);

    @Select("select worker_code, worker_name, ip, cpu, cpu_used, memory, memory_used, jvm_memory,"
            + "jvm_memory_used, job_num, heartbeat, app_name, update_time from kf_worker where app_name=#{appName}")
    List<KfWorkerPO> selectByAppName(@Param("appName") String appName);

    @Select("select count(1) from kf_worker where app_name=#{appName}")
    int countByAppName(@Param("appName") String appName);
}
