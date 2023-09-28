package com.didiglobal.knowframework.observability.conponent.metrics;

import com.didiglobal.knowframework.system.metrcis.Metrics;
import com.didiglobal.knowframework.system.metrcis.service.*;
import com.didiglobal.knowframework.system.metrcis.util.MathUtil;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.ObservabilityInitializer;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import org.apache.commons.collections4.MapUtils;
import java.util.Map;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public class PlatformMetricsInitializer implements ObservabilityInitializer {

    private Meter platformMeter = Observability.getMeter("metrics.platform");

    private SystemMetricsService systemMetricsService = Metrics.getMetricsServiceFactory().createSystemMetrics();
    private ProcessMetricsService processMetricsService = Metrics.getMetricsServiceFactory().createProcessMetrics();
    private DiskMetricsService diskMetricsService = Metrics.getMetricsServiceFactory().createDiskMetricsService();
    private DiskIOMetricsService diskIOMetricsService = Metrics.getMetricsServiceFactory().createDiskIOMetricsService();
    private NetCardMetricsService netCardMetricsService = Metrics.getMetricsServiceFactory().createNetCardMetricsService();

    public void startup() {

        /*
         * system metrics
         */
        platformMeter
                .gaugeBuilder("system.ntp.offset.gap")
                .setDescription("当前源时钟与本地时钟的时间差（单位：秒）")
                .setUnit(MetricUnit.METRIC_UNIT_SECOND)
                .buildWithCallback(
                        result -> result.record(MathUtil.divideWith2Digit(systemMetricsService.getSystemNtpOffset(), 1000L)));

        platformMeter
                .gaugeBuilder("system.cpu.cores")
                .setDescription("系统CPU核数")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemCpuCores()));

        platformMeter
                .gaugeBuilder("system.cpu.util")
                .setDescription("系统CPU使用率(单位：%)")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemCpuUtil()));

        platformMeter
                .gaugeBuilder("system.cpu.load.1")
                .setDescription("系统近1分钟平均负载（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemLoad1()));

        platformMeter
                .gaugeBuilder("system.cpu.steal")
                .setDescription("虚拟CPU等待实际CPU时间的百分比（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemCpuSteal()));

        platformMeter
                .gaugeBuilder("system.memory.total")
                .setDescription("系统内存总大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemMemTotal()));

        platformMeter
                .gaugeBuilder("system.memory.free")
                .setDescription("系统空闲内存大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemMemFree()));

        platformMeter
                .gaugeBuilder("system.memory.cached")
                .setDescription("普通文件所占用的缓存页（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemMemCached()));

        platformMeter
                .gaugeBuilder("system.memory.buffered")
                .setDescription("块设备占用的缓存页，包括直接读写块设备、文件系统元数据、superblock（超级块中的数据其实就是文件卷的控制信息部分，也可以说它是卷资源表，有关文件卷的大部分信息都保存在这里。例如：硬盘分区中每个block的大小、硬盘分区上一共有多少个block group、以及每个block group中有多少个inode）所使用的缓存页（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemMemBuffered()));

        platformMeter
                .gaugeBuilder("system.memory.swap.used")
                .setDescription("系统已用swap大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(systemMetricsService.getSystemSwapUsed()));

        //disk info
        Map<String, Long> path2BytesFreeMap = diskMetricsService.getBytesFree();
        Map<String, Long> path2BytesTotalMap = diskMetricsService.getBytesTotal();
        if (MapUtils.isNotEmpty(path2BytesFreeMap)) {
            path2BytesFreeMap.forEach((path, bytesFree) -> platformMeter
                    .gaugeBuilder("system.disk.free")
                    .setDescription("磁盘分区余量大小（单位：Byte）")
                    .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                    .buildWithCallback(
                            result -> result.record(
                                    bytesFree,
                                    Attributes.of(stringKey(Constant.METRIC_FIELD_NAME_DISK_PATH), path)
                            )
                    ));
        }
        if (MapUtils.isNotEmpty(path2BytesTotalMap)) {
            path2BytesTotalMap.forEach((path, bytesTotal) -> platformMeter
                    .gaugeBuilder("system.disk.total")
                    .setDescription("磁盘分区总量大小（单位：Byte）")
                    .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                    .buildWithCallback(
                            result -> result.record(
                                    bytesTotal,
                                    Attributes.of(stringKey(Constant.METRIC_FIELD_NAME_DISK_PATH), path)
                            )
                    ));
        }

        //disk io info
        Map<String, Double> device2IOUtilMap = diskIOMetricsService.getIOUtil();
        if (MapUtils.isNotEmpty(device2IOUtilMap)) {
            device2IOUtilMap.forEach((deviceName, ioUtil) -> platformMeter
                    .gaugeBuilder("system.io.util")
                    .setDescription("设备I/O请求的时间百分比（单位：%）")
                    .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                    .buildWithCallback(
                            result -> result.record(
                                    ioUtil,
                                    Attributes.of(stringKey(Constant.METRIC_FIELD_NAME_DEVICE_NAME), deviceName)
                            )
                    ));
        }
        Map<String, Double> device2AvgQuSzMap = diskIOMetricsService.getAvgQuSz();
        if(MapUtils.isNotEmpty(device2AvgQuSzMap)) {
            for (Map.Entry<String, Double> entry : device2AvgQuSzMap.entrySet()) {
                String deviceName = entry.getKey();
                Double avgQuSz = entry.getValue();
                platformMeter
                        .gaugeBuilder("system.io.avgqusz")
                        .setDescription("设备平均IO队列长度")
                        .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                        .buildWithCallback(
                                result -> result.record(
                                        avgQuSz,
                                        Attributes.of(stringKey(Constant.METRIC_FIELD_NAME_DEVICE_NAME), deviceName)
                                )
                        );
            }
        }

        platformMeter
                .gaugeBuilder("system.fd.files.max")
                .setDescription("系统可以打开的最大文件句柄数")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                systemMetricsService.getSystemFilesMax()
                        )
                );

        platformMeter
                .gaugeBuilder("system.fd.files.used.percent")
                .setDescription("系统使用文件句柄占已分配百分比（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(
                                systemMetricsService.getSystemFilesUsedPercent()
                        )
                );

        Map<String, Double> netCardDeviceName2ReceiveBytesPsMap = netCardMetricsService.getReceiveBytesPs();
        if(MapUtils.isNotEmpty(netCardDeviceName2ReceiveBytesPsMap)) {
            for(Map.Entry<String, Double> entry : netCardDeviceName2ReceiveBytesPsMap.entrySet()) {
                String netCardDeviceName = entry.getKey();
                Double receiveBytesPs = entry.getValue();
                platformMeter
                        .gaugeBuilder("system.net.card.receive.bytes.per.second")
                        .setDescription("网卡设备每秒下行流量（单位：Byte/秒）")
                        .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                        .buildWithCallback(
                                result -> result.record(
                                        receiveBytesPs,
                                        Attributes.of(stringKey(Constant.METRIC_FIELD_NAME_DEVICE_NAME), netCardDeviceName)
                                )
                        );
            }
        }

        Map<String, Double> netCardDeviceName2SendBytesPsMap = netCardMetricsService.getSendBytesPs();
        if(MapUtils.isNotEmpty(netCardDeviceName2SendBytesPsMap)) {
            for(Map.Entry<String, Double> entry : netCardDeviceName2SendBytesPsMap.entrySet()) {
                String netCardDeviceName = entry.getKey();
                Double sendBytesPs = entry.getValue();
                platformMeter
                        .gaugeBuilder("system.net.card.send.bytes.per.second")
                        .setDescription("网卡设备每秒上行流量（单位：Byte/秒）")
                        .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                        .buildWithCallback(
                                result -> result.record(
                                        sendBytesPs,
                                        Attributes.of(stringKey(Constant.METRIC_FIELD_NAME_DEVICE_NAME), netCardDeviceName)
                                )
                        );
            }
        }

        platformMeter
                .gaugeBuilder("system.net.work.receive.bytes.per.second")
                .setDescription("系统网络每秒下行流量（单位：Byte/秒）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                systemMetricsService.getSystemNetworkReceiveBytesPs()
                        )
                );

        platformMeter
                .gaugeBuilder("system.net.work.send.bytes.per.second")
                .setDescription("系统网络每秒上行流量（单位：Byte/秒）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                systemMetricsService.getSystemNetworkSendBytesPs()
                        )
                );

        platformMeter
                .gaugeBuilder("system.net.work.tcp.connection.num")
                .setDescription("系统tcp连接数")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                systemMetricsService.getSystemNetworkTcpConnectionNum()
                        )
                );

        /*
         * process metrics
         */
        platformMeter
                .gaugeBuilder("process.run.time")
                .setDescription("进程运行时间（单位：小时）")
                .setUnit(MetricUnit.METRIC_UNIT_HOUR)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcUptime()
                        )
                );

        platformMeter
                .gaugeBuilder("process.cpu.util")
                .setDescription("进程CPU使用率(单位：%) ")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcCpuUtil()
                        )
                );

        platformMeter
                .gaugeBuilder("process.cpu.system")
                .setDescription("进程内核态cpu使用率（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcCpuSys()
                        )
                );

        platformMeter
                .gaugeBuilder("process.cpu.user")
                .setDescription("进程用户态cpu使用率（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcCpuUser()
                        )
                );

        platformMeter
                .gaugeBuilder("process.cpu.switches.per.second")
                .setDescription("进程CPU每秒上下文交换次数")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcCpuSwitchesPS()
                        )
                );

        platformMeter
                .gaugeBuilder("process.cpu.voluntary.switches.per.second")
                .setDescription("当前进程CPU每秒自愿上下文交换次数（自愿上下文切换，是指进程无法获取所需资源，导致的上下文切换。比如说， I/O、内存等系统资源不足时，就会发生自愿上下文切换）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcCpuVoluntarySwitchesPS()
                        )
                );

        platformMeter
                .gaugeBuilder("process.cpu.non.voluntary.switches.per.second")
                .setDescription("当前进程CPU每秒非自愿上下文交换次数（非自愿上下文切换，则是指进程由于时间片已到等原因，被系统强制调度，进而发生的上下文切换。比如说，大量进程都在争抢 CPU 时，就容易发生非自愿上下文切换）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcCpuNonVoluntarySwitchesPS()
                        )
                );

        platformMeter
                .gaugeBuilder("process.memory.used")
                .setDescription("当前进程内存使用量（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcMemUsed()
                        )
                );

        platformMeter
                .gaugeBuilder("process.memory.util")
                .setDescription("当前进程内存使用率（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcMemUtil()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.heap.memory.used")
                .setDescription("JVM进程堆内存使用量（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcHeapMemoryUsed()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.non.heap.memory.used")
                .setDescription("JVM进程堆外内存使用量（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcNonHeapMemoryUsed()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.heap.size.xmx")
                .setDescription("JVM进程最大可用堆内存，对应 JVM 启动参数 Xmx 属性值！（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcHeapSizeXmx()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.memory.used.peak")
                .setDescription("JVM进程启动以来内存使用量峰值（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcMemUsedPeak()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.heap.memory.used.percent")
                .setDescription("JVM堆内存使用率（单位：%）")
                .setUnit(MetricUnit.METRIC_UNIT_PERCENT)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcHeapMemUsedPercent()
                        )
                );

        platformMeter
                .gaugeBuilder("process.io.read.bytes.per.second")
                .setDescription("当前进程IO读取速率（单位：Byte/s）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcIOReadBytesRate()
                        )
                );

        platformMeter
                .gaugeBuilder("process.io.write.bytes.per.second")
                .setDescription("当前进程IO写入速率（单位：Byte/s）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcIOWriteBytesRate()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.young.gc.count.last.1.minute")
                .setDescription("JVM进程在最近一次指标上报周期内的 Young GC 次数（单位：次）")
                .setUnit(MetricUnit.METRIC_UNIT_TIME)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcYoungGcCount()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.full.gc.count.last.1.minute")
                .setDescription("JVM进程最近一次指标上报周期内 Full GC 次数（单位：次）")
                .setUnit(MetricUnit.METRIC_UNIT_TIME)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcFullGcCount()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.young.gc.time.last.1.minute")
                .setDescription("JVM进程最近一次指标上报周期内 Young GC 耗时（单位：毫秒）")
                .setUnit(MetricUnit.METRIC_UNIT_MILLISECOND)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcYoungGcTime()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.full.gc.time.last.1.minute")
                .setDescription("JVM进程最近一次指标上报周期内 Full GC 耗时（单位：毫秒）")
                .setUnit(MetricUnit.METRIC_UNIT_MILLISECOND)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcFullGcTime()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.s0c")
                .setDescription("JVM进程第一个幸存区大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcS0C()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.s1c")
                .setDescription("JVM进程第二个幸存区大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcS1C()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.s0u")
                .setDescription("JVM进程第一个幸存区使用量大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcS0U()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.s1u")
                .setDescription("JVM进程第二个幸存区使用量大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcS1U()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.ec")
                .setDescription("JVM进程 Eden 区大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcEC()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.eu")
                .setDescription("JVM进程 Eden 区使用量大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcEU()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.oc")
                .setDescription("JVM进程老年代大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcOC()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.ou")
                .setDescription("JVM进程老年代使用量大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcOU()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.mc")
                .setDescription("JVM进程方法区大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcMC()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.mu")
                .setDescription("JVM进程方法区使用量大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcMU()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.ccsc")
                .setDescription("JVM进程压缩类空间大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcCCSC()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.ccsu")
                .setDescription("JVM进程压缩类空间使用量大小（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcCCSU()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.thread.num")
                .setDescription("JVM进程内的线程总数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcThreadNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.jvm.thread.num.peak")
                .setDescription("JVM进程启动以来线程数峰值（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getJvmProcThreadNumPeak()
                        )
                );

        platformMeter
                .gaugeBuilder("process.open.fd.count")
                .setDescription("进程已打开的 FD 数量（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcOpenFdCount()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.receive.bytes.per.second")
                .setDescription("进程每秒下行流量（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkReceiveBytesPs()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.send.bytes.per.second")
                .setDescription("进程每秒上行流量（单位：Byte）")
                .setUnit(MetricUnit.METRIC_UNIT_BYTES)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkSendBytesPs()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.connection.num")
                .setDescription("进程 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpConnectionNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.listening.num")
                .setDescription("进程处于 LISTENING 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpListeningNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.time.wait.num")
                .setDescription("进程处于 TIME_WAIT 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpTimeWaitNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.close.wait.num")
                .setDescription("进程处于 CLOSE_WAIT 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpCloseWaitNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.established.num")
                .setDescription("进程处于 ESTABLISHED 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpEstablishedNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.syn.sent.num")
                .setDescription("进程处于 SYN_SENT 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpSynSentNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.syn.recv.num")
                .setDescription("进程处于 SYN_RECV 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpSynRecvNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.fin.wait.1.num")
                .setDescription("进程处于 FIN_WAIT_1 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpFinWait1Num()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.fin.wait.2.num")
                .setDescription("进程处于 FIN_WAIT_2 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpFinWait2Num()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.closed.num")
                .setDescription("进程处于 CLOSED 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpClosedNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.closing.num")
                .setDescription("进程处于 CLOSING 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpClosingNum()
                        )
                );

        platformMeter
                .gaugeBuilder("process.net.work.tcp.last.ack.num")
                .setDescription("进程处于 LAST_ACK 状态的 TCP 连接数（单位：个）")
                .setUnit(MetricUnit.METRIC_UNIT_NUMBER)
                .buildWithCallback(
                        result -> result.record(
                                processMetricsService.getProcNetworkTcpLastAckNum()
                        )
                );

    }

}
