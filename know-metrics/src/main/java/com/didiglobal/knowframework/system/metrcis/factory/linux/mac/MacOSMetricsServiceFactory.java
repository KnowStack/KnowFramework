package com.didiglobal.knowframework.system.metrcis.factory.linux.mac;

import com.didiglobal.knowframework.system.metrcis.exception.MetricsException;
import com.didiglobal.knowframework.system.metrcis.factory.MetricsServiceFactory;
import com.didiglobal.knowframework.system.metrcis.service.*;
import com.didiglobal.knowframework.system.metrcis.service.macos.*;

import java.util.Map;

public class MacOSMetricsServiceFactory implements MetricsServiceFactory {
    @Override
    public SystemMetricsService createSystemMetrics() throws MetricsException {
        return new MacOSSystemMetricsServiceImpl();
    }

    @Override
    public ProcessMetricsService createProcessMetrics() {
        return new MacOSProcessMetricsServiceImpl();
    }

    @Override
    public DiskIOMetricsService createDiskIOMetricsService() {
        return new MacOSDiskIOMetricsServiceImpl();
    }

    @Override
    public DiskMetricsService createDiskMetricsService() {
        return new MacOSDiskMetricsServiceImpl();
    }

    @Override
    public NetCardMetricsService createNetCardMetricsService() {
        return new MacOSNetCardMetricsServiceImpl();
    }

    @Override
    public Map<Class, Object> getMetricsServiceMap() {
        return null;
    }
}
