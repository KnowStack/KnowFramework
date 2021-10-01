package com.didichuxing.datachannel.metrics.helper.convert;

public interface ConversionService {
    <T> T convert(Object source, Class<T> targetType);
}
