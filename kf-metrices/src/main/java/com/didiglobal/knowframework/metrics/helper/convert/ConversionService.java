package com.didiglobal.knowframework.metrics.helper.convert;

public interface ConversionService {
    <T> T convert(Object source, Class<T> targetType);
}
