package com.didiglobal.knowframework.system.metrcis.service.macos;

import com.didiglobal.knowframework.system.metrcis.bean.PeriodStatistics;
import com.didiglobal.knowframework.system.metrcis.service.DiskIOMetricsService;

import java.util.HashMap;
import java.util.Map;

public class MacOSDiskIOMetricsServiceImpl implements DiskIOMetricsService {

    @Override
    public Map<String, Double> getAvgQuSz() {
        Map<String, Double> map = new HashMap<>();
        map.put("device0", 0d);
        return map;
    }

    @Override
    public Map<String, PeriodStatistics> getAvgRqSz() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOAwait() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIORAwait() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadRequest() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadBytes() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIORRQMS() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOSVCTM() {
        return null;
    }

    @Override
    public Map<String, Double> getIOUtil() {
        Map<String, Double> map = new HashMap<>();
        map.put("device0", 0d);
        return map;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWAwait() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWriteRequest() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWriteBytes() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadWriteBytes() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWRQMS() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskReadTime() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskReadTimePercent() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskWriteTime() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskWriteTimePercent() {
        return null;
    }

}
