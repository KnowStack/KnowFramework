package com.didiglobal.knowframework.job.core.task;

import com.didiglobal.knowframework.job.common.dto.KfTaskLockDTO;
import com.didiglobal.knowframework.job.KfJobProperties;
import com.didiglobal.knowframework.job.common.po.KfTaskLockPO;
import com.didiglobal.knowframework.job.core.WorkerSingleton;
import com.didiglobal.knowframework.job.mapper.KfTaskLockMapper;
import com.didiglobal.knowframework.job.utils.BeanUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * task lock service.
 *
 * @author ds
 */
@Service
public class TaskLockServiceImpl implements TaskLockService {
    private static final Logger logger = LoggerFactory.getLogger(TaskLockServiceImpl.class);
    // 每次申请锁的默认过期时间
    private static final Long EXPIRE_TIME_SECONDS = 300L;

    private KfTaskLockMapper kfTaskLockMapper;
    private KfJobProperties kfJobProperties;

    /**
     * constructor
     * @param kfTaskLockMapper mapper
     * @param kfJobProperties 配置信息
     */
    @Autowired
    public TaskLockServiceImpl(KfTaskLockMapper kfTaskLockMapper, KfJobProperties kfJobProperties) {
        this.kfTaskLockMapper = kfTaskLockMapper;
        this.kfJobProperties = kfJobProperties;
    }

    @Override
    public Boolean tryAcquire(String taskCode) {
        return tryAcquire(taskCode, WorkerSingleton.getInstance().getKfWorker().getWorkerCode(),
                EXPIRE_TIME_SECONDS);
    }

    @Override
    public Boolean tryAcquire(String taskCode, String workerCode, Long expireTime) {
        List<KfTaskLockPO> kfTaskLockPOList =
                kfTaskLockMapper.selectByTaskCode(taskCode, kfJobProperties.getAppName());

        boolean hasLock = false;
        //1、taskCode没有任何worker占有
        if (CollectionUtils.isEmpty( kfTaskLockPOList )) {
            hasLock = false;
        } else {
            //2、taskCode有被worker占有
            long current = System.currentTimeMillis() / 1000;

            //3、taskCode的worker是否有没有过期
            List<KfTaskLockPO> noExpireTaskLock = kfTaskLockPOList.stream().filter( kfTaskLockPO -> kfTaskLockPO.getCreateTime()
                    .getTime() / 1000 + kfTaskLockPO.getExpireTime() >= current).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(noExpireTaskLock)){
                for(KfTaskLockPO kfTaskLockPO : noExpireTaskLock){
                    if(workerCode.equals( kfTaskLockPO.getWorkerCode())){
                        hasLock = true;
                    }
                }
            }

            //4、taskCode的worker是否有过期
            List<KfTaskLockPO> expireTaskLock = kfTaskLockPOList.stream().filter( kfTaskLockPO -> kfTaskLockPO.getCreateTime()
                    .getTime() / 1000 + kfTaskLockPO.getExpireTime() < current).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(expireTaskLock)){
                for(KfTaskLockPO kfTaskLockPO : expireTaskLock){
                    kfTaskLockMapper.deleteByWorkerCodeAndAppName( kfTaskLockPO.getWorkerCode(), kfJobProperties.getAppName());
                }
            }
        }

        if (!hasLock) {
            KfTaskLockPO taskLock = new KfTaskLockPO();
            taskLock.setTaskCode(taskCode);
            taskLock.setWorkerCode(workerCode);
            taskLock.setExpireTime(expireTime);
            taskLock.setCreateTime(new Timestamp(System.currentTimeMillis()));
            taskLock.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            taskLock.setAppName( kfJobProperties.getAppName());
            try {
                return kfTaskLockMapper.insert(taskLock) > 0 ? true : false;
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    logger.info("class=TaskLockServiceImpl||method=tryAcquire||taskCode={}||msg=duplicate key", taskCode);
                } else {
                    logger.error(
                            "class=TaskLockServiceImpl||method=tryAcquire||taskCode={}||msg={}", taskCode, e.getMessage()
                    );
                }
                return false;
            }
        }
        return hasLock;
    }

    @Override
    public Boolean tryRelease(String taskCode) {
        return tryRelease(taskCode, WorkerSingleton.getInstance().getKfWorker().getWorkerCode());
    }

    @Override
    public Boolean tryRelease(String taskCode, String workerCode) {
        List<KfTaskLockPO> kfTaskLockPOList = kfTaskLockMapper.selectByTaskCodeAndWorkerCode(taskCode,
                workerCode, kfJobProperties.getAppName());
        if (CollectionUtils.isEmpty( kfTaskLockPOList )) {
            logger.error("class=TaskLockServiceImpl||method=tryRelease||msg=taskCode={}, "
                    + "workerCode={}", taskCode, workerCode);
            return false;
        }
        long current = System.currentTimeMillis() / 1000;
        List<Long> taskLockIdList = kfTaskLockPOList.stream().filter( kfTaskLockPO ->
                        kfTaskLockPO.getCreateTime().getTime() / 1000 + kfTaskLockPO.getExpireTime() < current)
                .map( KfTaskLockPO::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(taskLockIdList)) {
            return true;
        }

        int result = kfTaskLockMapper.deleteByIds(taskLockIdList);
        return result > 0 ? true : false;
    }

    @Override
    public List<KfTaskLockDTO> getAll() {
        List<KfTaskLockPO> kfTaskLockPOS = kfTaskLockMapper.selectByAppName( kfJobProperties.getAppName());
        if (CollectionUtils.isEmpty( kfTaskLockPOS )) {
            return null;
        }
        return kfTaskLockPOS.stream().map( kfTaskLockPO -> BeanUtil.convertTo( kfTaskLockPO,
                KfTaskLockDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void renewAll() {

    }
}
