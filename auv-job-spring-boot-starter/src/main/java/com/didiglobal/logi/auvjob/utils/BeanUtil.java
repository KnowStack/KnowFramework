package com.didiglobal.logi.auvjob.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {
  private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

  /**
   * 对象转换为目标类对象.
   *
   * @param source 源
   * @param targetClass 目标类
   * @param <T> 目标对象
   * @return 转换到的对象
   */
  public static <T> T convertTo(Object source, Class<T> targetClass) {
    T instance = null;
    try {
      instance = targetClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      logger.error("class=BeanUtil||method=convertTo||url=||msg={}", e);
    }
    copyProperties(source, instance);
    return instance;
  }

  /**
   * copy属性.
   *
   * @param source 源
   * @param target 目标
   */
  private static void copyProperties(Object source, Object target) {
    Assert.notNull(source, "Source must not be null");
    Assert.notNull(target, "Target must not be null");

    Class<?> sourceClass = source.getClass();
    Field[] sourceFields = sourceClass.getDeclaredFields();
    Map<String, Object> sourceFieldMap = new HashMap<>();
    try {
      for (Field sourceField : sourceFields) {
        sourceField.setAccessible(true);
        sourceFieldMap.put(sourceField.getName(), sourceField.get(source));
      }
    } catch (IllegalAccessException e) {
      logger.error("class=BeanUtil||method=copyProperties||url=||msg={}", e);
    }

    Field[] targetFields = target.getClass().getDeclaredFields();
    try {
      for (Field targetField : targetFields) {
        String targetFieldName = targetField.getName();
        if (sourceFieldMap.containsKey(targetFieldName)) {
          targetField.setAccessible(true);
          targetField.set(target, sourceFieldMap.get(targetFieldName));
        }
      }
    } catch (Exception e) {
      logger.error("class=BeanUtil||method=copyProperties||url=||msg={}", e);
    }
  }
}