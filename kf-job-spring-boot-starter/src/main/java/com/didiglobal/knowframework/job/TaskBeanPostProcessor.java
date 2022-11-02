package com.didiglobal.knowframework.job;

import com.didiglobal.knowframework.job.annotation.Task;
import com.didiglobal.knowframework.job.common.enums.TaskStatusEnum;
import com.didiglobal.knowframework.job.common.po.KfTaskPO;
import com.didiglobal.knowframework.job.core.job.Job;
import com.didiglobal.knowframework.job.core.job.JobFactory;
import com.didiglobal.knowframework.job.mapper.KfTaskMapper;
import com.didiglobal.knowframework.job.utils.CronExpression;
import com.didiglobal.knowframework.job.utils.IdWorker;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TaskBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TaskBeanPostProcessor.class);

    private static Map<String, KfTaskPO> taskMap = new HashMap<>();

    @Autowired
    private KfTaskMapper kfTaskMapper;

    @Autowired
    private JobFactory jobFactory;

    @Autowired
    private KfJobProperties kfJobProperties;

    @PostConstruct
    public void init(){
        logger.info("class=TaskBeanPostProcessor||method=init");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if(!kfJobProperties.getEnable()){
                return bean;
            }

            Class<?> beanClass = bean.getClass();
            // add job to jobFactory
            if (bean instanceof Job) {
                jobFactory.addJob(beanClass.getCanonicalName(), (Job) bean);
            } else {
                return bean;
            }

            // check and register to db

            Task taskAnnotation = beanClass.getAnnotation(Task.class);
            if (taskAnnotation == null || !taskAnnotation.autoRegister()) {
                return bean;
            }
            // check
            if (!check(taskAnnotation)) {
                logger.error("class=TaskBeanPostProcessor||method=blacklist||url=||msg=invalid schedule {}",
                        taskAnnotation.toString());
            }

            if(!contains(beanClass.getCanonicalName())){
                KfTaskPO task = getNewLogTask(beanClass, taskAnnotation);
                task.setTaskCode(IdWorker.getIdStr());
                task.setStatus(TaskStatusEnum.RUNNING.getValue());
                kfTaskMapper.insert(task);
            }else {
                KfTaskPO task = taskMap.get(beanClass.getCanonicalName());
                task = updateLogTask(task, beanClass, taskAnnotation);
                kfTaskMapper.updateByCode(task);
            }
        }catch (Exception e){
            logger.error("class=TaskBeanPostProcessor||method=postProcessAfterInitialization||beanName={}||msg=exception",
                    beanName, e);
        }

        return bean;
    }

    /*********************************************** private method ***********************************************/
    private boolean check(Task schedule) {
        return CronExpression.isValidExpression(schedule.cron());
    }

    private KfTaskPO getNewLogTask(Class<?> beanClass, Task schedule) {
        KfTaskPO kfTaskPO = new KfTaskPO();
        kfTaskPO.setTaskName(schedule.name());
        kfTaskPO.setTaskDesc(schedule.description());
        kfTaskPO.setCron(schedule.cron());
        kfTaskPO.setClassName(beanClass.getCanonicalName());
        kfTaskPO.setParams("");
        kfTaskPO.setRetryTimes(schedule.retryTimes());
        kfTaskPO.setLastFireTime(new Timestamp(System.currentTimeMillis()));
        kfTaskPO.setTimeout(schedule.timeout());
        kfTaskPO.setSubTaskCodes("");
        kfTaskPO.setConsensual(schedule.consensual().name());
        kfTaskPO.setTaskWorkerStr("");
        kfTaskPO.setAppName( kfJobProperties.getAppName());
        kfTaskPO.setOwner(schedule.owner());
        return kfTaskPO;
    }

    private KfTaskPO updateLogTask(KfTaskPO kfTaskPO, Class<?> beanClass, Task schedule){
        kfTaskPO.setTaskName(schedule.name());
        kfTaskPO.setTaskDesc(schedule.description());
        kfTaskPO.setCron(schedule.cron());
        kfTaskPO.setClassName(beanClass.getCanonicalName());
        kfTaskPO.setParams("");
        kfTaskPO.setRetryTimes(schedule.retryTimes());
        kfTaskPO.setTimeout(schedule.timeout());
        kfTaskPO.setConsensual(schedule.consensual().name());
        kfTaskPO.setAppName( kfJobProperties.getAppName());
        kfTaskPO.setOwner(schedule.owner());
        return kfTaskPO;
    }

    private boolean contains(String className) {
        if (taskMap.isEmpty()) {
            List<KfTaskPO> kfTaskPOS = kfTaskMapper.selectByAppName( kfJobProperties.getAppName());
            taskMap = kfTaskPOS.stream().collect(Collectors.toMap( KfTaskPO::getClassName,
                    Function.identity()));
        }
        return taskMap.containsKey(className);
    }
}