package com.didiglobal.logi.metrics.helper.convert;

/**
 * 
 * 
 * @author liujianhui
 */
public interface ConverterRegistry {

    void addConverter(Converter<?, ?> converter);

    void addConverter(Class<?> sourceType, Class<?> targetType, Converter<?, ?> converter);
}
