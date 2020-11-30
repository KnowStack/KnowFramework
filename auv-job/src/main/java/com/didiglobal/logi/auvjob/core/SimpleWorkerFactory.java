package com.didiglobal.logi.auvjob.core;

import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

/**
 * worker创建工厂.
 *
 * @author dengshan
 */
public class SimpleWorkerFactory implements WorkerFactory {
  private static final Logger logger = LoggerFactory.getLogger(SimpleWorkerFactory.class);

  @Override
  public Scheduler instantiate() {
    return null;
  }

  @Override
  public WorkerInfo newWorker() {
    WorkerInfo workerInfo = new WorkerInfo();
    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      logger.error("class=SimpleWorkerFactory||method=newWorker||url=||msg={}", e);
    }

    workerInfo.setCode(inetAddress.getHostAddress());
    workerInfo.setName(inetAddress.getHostName());

    DecimalFormat df = new DecimalFormat("#.000");

    SystemInfo systemInfo = new SystemInfo();
    // cpu
    CentralProcessor processor = systemInfo.getHardware().getProcessor();
    long[] prevTicks = processor.getSystemCpuLoadTicks();
    long[] ticks = processor.getSystemCpuLoadTicks();
    long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
            - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
    long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
    long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
    long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
            - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
    long csys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
            - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
    long user = ticks[CentralProcessor.TickType.USER.getIndex()]
            - prevTicks[CentralProcessor.TickType.USER.getIndex()];
    long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
            - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
    long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
            - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
    long totalCpu = user + nice + csys + idle + ioWait + irq + softIrq + steal;
    workerInfo.setCpu(processor.getLogicalProcessorCount());
    workerInfo.setCpuUsed(1.0 - (idle * 1.0 / totalCpu));

    // memory
    GlobalMemory memory = systemInfo.getHardware().getMemory();
    Double totalMemory = memory.getTotal() * 1.0 / 1024 / 1024;
    workerInfo.setMemory(Double.valueOf(df.format(totalMemory)));
    Double memoryUsed = (memory.getTotal() - memory.getAvailable()) * 1.0 / memory.getTotal();
    workerInfo.setMemoryUsed(Double.valueOf(df.format(memoryUsed)));

    Runtime runtime = Runtime.getRuntime();
    Double jvmMemory = runtime.totalMemory() * 1.0 / 1024 / 1024;
    workerInfo.setJvmMemory(Double.valueOf(df.format(jvmMemory)));
    Double jvmMemoryUsed = (runtime.totalMemory() - runtime.freeMemory()) * 1.0
            / runtime.totalMemory();
    workerInfo.setJvmMemoryUsed(Double.valueOf(df.format(jvmMemoryUsed)));

    // workerInfo.setJobNum();

    workerInfo.setHeartbeat(LocalDateTime.now());
    return workerInfo;
  }
}
