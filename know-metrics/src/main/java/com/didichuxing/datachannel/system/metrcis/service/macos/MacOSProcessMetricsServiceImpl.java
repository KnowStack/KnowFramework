package com.didichuxing.datachannel.system.metrcis.service.macos;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;

import java.util.List;
import java.util.Random;

public class MacOSProcessMetricsServiceImpl implements ProcessMetricsService {
    @Override
    public Long getProcessStartupTime() {
        return System.currentTimeMillis();
    }

    @Override
    public Long getProcUptime() {
        return System.currentTimeMillis();
    }

    @Override
    public Long getProcessPid() {
        return new Random().nextLong();
    }

    @Override
    public Double getProcCpuUtil() {
        return 0d;
    }

    @Override
    public Double getCurrentProcCpuUtil() {
        return 20d;
    }

    @Override
    public PeriodStatistics getProcCpuUtilTotalPercent() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Double getProcCpuSys() {
        return 0d;
    }

    @Override
    public Double getProcCpuUser() {
        return 0d;
    }

    @Override
    public Double getProcCpuSwitchesPS() {
        return 0d;
    }

    @Override
    public Double getProcCpuVoluntarySwitchesPS() {
        return 0d;
    }

    @Override
    public Double getProcCpuNonVoluntarySwitchesPS() {
        return 0d;
    }

    @Override
    public Long getProcMemUsed() {
        return 1 * 1024 * 1024 * 1024L;
    }

    @Override
    public Double getProcMemUtil() {
        return 10d;
    }

    @Override
    public Long getProcMemData() {
        return null;
    }

    @Override
    public Long getProcMemDirty() {
        return null;
    }

    @Override
    public Long getProcMemLib() {
        return null;
    }

    @Override
    public Long getProcMemRss() {
        return null;
    }

    @Override
    public Long getProcMemShared() {
        return null;
    }

    @Override
    public Long getProcMemSwap() {
        return null;
    }

    @Override
    public Long getProcMemText() {
        return null;
    }

    @Override
    public Long getProcMemVms() {
        return null;
    }

    @Override
    public Long getJvmProcHeapMemoryUsed() {
        return 500 * 1024 * 1024L;
    }

    @Override
    public Long getJvmProcNonHeapMemoryUsed() {
        return 500 * 1024 * 1024L;
    }

    @Override
    public Long getJvmProcHeapSizeXmx() {
        return 2 * 1024 * 1024 * 1024L;
    }

    @Override
    public Long getJvmProcMemUsedPeak() {
        return 1 * 1024 * 1024 * 1024L;
    }

    @Override
    public Double getJvmProcHeapMemUsedPercent() {
        return 1d;
    }

    @Override
    public PeriodStatistics getProcIOReadRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Double getProcIOReadBytesRate() {
        return 0d;
    }

    @Override
    public PeriodStatistics getProcIOWriteRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Double getProcIOWriteBytesRate() {
        return 0d;
    }

    @Override
    public PeriodStatistics getProcIOReadWriteRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public PeriodStatistics getProcIOReadWriteBytesRate() {
        return null;
    }

    @Override
    public PeriodStatistics getProcIOAwaitTimePercent() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Long getJvmProcYoungGcCount() {
        return 10L;
    }

    @Override
    public Long getJvmProcFullGcCount() {
        return 5L;
    }

    @Override
    public Long getJvmProcYoungGcTime() {
        return 10000L;
    }

    @Override
    public Long getJvmProcFullGcTime() {
        return 100000L;
    }

    @Override
    public Double getJvmProcS0C() {
        return 0d;
    }

    @Override
    public Double getJvmProcS1C() {
        return 0d;
    }

    @Override
    public Double getJvmProcS0U() {
        return 0d;
    }

    @Override
    public Double getJvmProcS1U() {
        return 0d;
    }

    @Override
    public Double getJvmProcEC() {
        return 0d;
    }

    @Override
    public Double getJvmProcEU() {
        return 0d;
    }

    @Override
    public Double getJvmProcOC() {
        return 0d;
    }

    @Override
    public Double getJvmProcOU() {
        return 0d;
    }

    @Override
    public Double getJvmProcMC() {
        return 0d;
    }

    @Override
    public Double getJvmProcMU() {
        return 0d;
    }

    @Override
    public Double getJvmProcCCSC() {
        return 0d;
    }

    @Override
    public Double getJvmProcCCSU() {
        return 0d;
    }

    @Override
    public Integer getJvmProcThreadNum() {
        return 5;
    }

    @Override
    public Integer getJvmProcThreadNumPeak() {
        return 100;
    }

    @Override
    public Integer getProcOpenFdCount() {
        return 999;
    }

    @Override
    public List<Integer> getProcPortListen() {
        return null;
    }

    @Override
    public Double getProcNetworkReceiveBytesPs() {
        return 0d;
    }

    @Override
    public Double getProcNetworkSendBytesPs() {
        return 0d;
    }

    @Override
    public PeriodStatistics getProcNetworkConnRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Integer getProcNetworkTcpConnectionNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpListeningNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpEstablishedNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpSynSentNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpSynRecvNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpFinWait1Num() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpFinWait2Num() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpTimeWaitNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpClosedNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpCloseWaitNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpClosingNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpLastAckNum() {
        return 0;
    }

    @Override
    public Integer getProcNetworkTcpNoneNum() {
        return 0;
    }

}
