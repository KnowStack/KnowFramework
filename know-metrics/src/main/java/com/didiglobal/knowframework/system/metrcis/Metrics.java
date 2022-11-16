package com.didiglobal.knowframework.system.metrcis;

import com.didiglobal.knowframework.system.metrcis.constant.ExceptionCodeEnum;
import com.didiglobal.knowframework.system.metrcis.constant.OSTypeEnum;
import com.didiglobal.knowframework.system.metrcis.exception.MetricsException;
import com.didiglobal.knowframework.system.metrcis.factory.MetricsServiceFactory;
import com.didiglobal.knowframework.system.metrcis.factory.linux.LinuxMetricsServiceFactory;
import com.didiglobal.knowframework.system.metrcis.factory.linux.mac.MacOSMetricsServiceFactory;

import java.lang.management.ManagementFactory;

/**
 * 入口类
 */
public class Metrics {

    public static MetricsServiceFactory getMetricsServiceFactory() {
        //根据 os 类型进行对应实例化
        String osName = ManagementFactory.getOperatingSystemMXBean().getName().toLowerCase();
        if (osName.contains(OSTypeEnum.LINUX.getDesc())) {
            return LinuxMetricsServiceFactory.getInstance();
        } else if (osName.contains(OSTypeEnum.AIX.getDesc())) {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        } else if (osName.contains(OSTypeEnum.WINDOWS.getDesc())) {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        } else if (osName.contains(OSTypeEnum.MAC_OS.getDesc())) {
            return new MacOSMetricsServiceFactory();
        } else {
            throw new MetricsException(String.format(
                    "class=Metrics||method=getMetricsServiceFactory||errMsg=os={%s} not support",
                    osName), ExceptionCodeEnum.SYSTEM_NOT_SUPPORT.getCode());
        }
    }

}
