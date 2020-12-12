package com.didiglobal.logi.auvjob;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.auvjob.annotation.Schedule;
import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.enums.TaskStatusEnum;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobFactory;
import com.didiglobal.logi.auvjob.mapper.AuvTaskMapper;
import com.didiglobal.logi.auvjob.utils.CronExpression;
import java.time.LocalDateTime;
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

@Component
public class ScheduleBeanPostProcessor implements BeanPostProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleBeanPostProcessor.class);

  private static Map<String, AuvTask> taskMap = new HashMap<>();

  @Autowired
  private AuvTaskMapper auvTaskMapper;

  @Autowired
  private JobFactory jobFactory;

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> beanClass = bean.getClass();
    // add job to jobFactory
    if (bean instanceof Job) {
      jobFactory.addJob(beanClass.getCanonicalName(), (Job) bean);
    } else {
      return bean;
    }

    // check and register to db

    Schedule scheduleAnnotation = beanClass.getAnnotation(Schedule.class);
    if (scheduleAnnotation == null || !scheduleAnnotation.autoRegister()) {
      return bean;
    }
    // check
    if (!check(scheduleAnnotation)) {
      LOGGER.error("invalid schedule {}", scheduleAnnotation.toString());
    }
    // not exists register
    AuvTask task = getAuvTask(beanClass, scheduleAnnotation);
    if (!contains(task)) {
      auvTaskMapper.insert(task);
    }
    return bean;
  }

  //########################## private method #################################

  private boolean check(Schedule schedule) {
    return CronExpression.isValidExpression(schedule.cron());
  }

  private AuvTask getAuvTask(Class<?> beanClass, Schedule schedule) {
    AuvTask auvTask = new AuvTask();
    auvTask.setName(schedule.name());
    auvTask.setDescription(schedule.description());
    auvTask.setCron(schedule.cron());
    auvTask.setClassName(beanClass.getCanonicalName());
    auvTask.setParams("");
    auvTask.setRetryTimes(schedule.retryTimes());
    auvTask.setLastFireTime(LocalDateTime.now());
    auvTask.setTimeout(schedule.timeout());
    auvTask.setSubTaskCodes("");
    return auvTask;
  }

  private boolean contains(AuvTask task) {
    if (taskMap.isEmpty()) {
      List<AuvTask> auvTasks = auvTaskMapper.selectList(new QueryWrapper<AuvTask>().ne("status",
              TaskStatusEnum.STOPPED.getValue()));
      taskMap = auvTasks.stream().collect(Collectors.toMap(AuvTask::getClassName,
              Function.identity()));
    }
    return taskMap.containsKey(task.getClassName());
  }
}