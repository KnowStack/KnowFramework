package com.didiglobal.knowframework.job.mapper;

import com.didiglobal.knowframework.job.common.po.KfJobPO;

import java.util.List;

import org.apache.ibatis.annotations.*;

/**
 * <p>
 * 正在执行的job信息 Mapper 接口.
 * </p>
 *
 * @author ds
 * @since 2020-11-10
 */
@Mapper
public interface KfJobMapper {

    @Delete("delete from kf_job where job_code=#{code}")
    int deleteByCode(String code);

    @Insert("INSERT INTO kf_job(job_code, task_code, class_name, try_times, worker_code, start_time, "
            + "create_time, update_time, app_name) VALUES(#{jobCode}, #{taskCode}, #{className}, #{tryTimes}, "
            + "#{workerCode}, #{startTime}, #{createTime}, #{updateTime}, #{appName})")
    int insert(KfJobPO kfJobPO);

    @Select("select id, job_code, task_code, class_name, try_times, worker_code, start_time, create_time,"
            + " update_time, app_name from kf_job")
    List<KfJobPO> selectAll();

    @Select("select id, job_code, task_code, class_name, try_times, worker_code, start_time, create_time,"
            + " update_time, app_name from kf_job where app_name=#{appName}")
    List<KfJobPO> selectByAppName(@Param("appName") String appName);


    @Select("select id, job_code, task_code, class_name, try_times, worker_code, start_time, create_time,"
            + " update_time, app_name from kf_job where job_code=#{JobCode} and app_name=#{appName}")
    KfJobPO selectByCode(@Param("code") String code, @Param("appName") String appName);

}
