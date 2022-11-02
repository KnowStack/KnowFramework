package com.didiglobal.knowframework.job.mapper;

import com.didiglobal.knowframework.job.common.po.KfWorkerBlacklistPO;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * worker黑名单列表信息 Mapper 接口.
 * </p>
 *
 * @author ds
 * @since 2020-11-10
 */
public interface KfWorkerBlacklistMapper {

    @Insert("INSERT INTO kf_worker_blacklist(worker_code) VALUES(#{workerCode})")
    int insert(KfWorkerBlacklistPO kfWorkerBlacklistPO);

    @Delete("delete from kf_worker_blacklist where worker_code=#{workerCode}")
    int deleteByWorkerCode(@Param("workerCode") String workerCode);

    @Select("select id, worker_code from kf_worker_blacklist")
    List<KfWorkerBlacklistPO> selectAll();

}
