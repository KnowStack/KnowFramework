package com.didiglobal.knowframework.job.core;

import com.didiglobal.knowframework.job.core.monitor.BeatMonitor;
import com.didiglobal.knowframework.job.core.monitor.MisfireMonitor;
import com.didiglobal.knowframework.job.core.monitor.TaskMonitor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认worker实现.
 *
 * @author ds
 */
public class SimpleScheduler implements Scheduler {

    private BeatMonitor beatMonitor;
    private TaskMonitor taskMonitor;
    private MisfireMonitor misfireMonitor;

    /**
     * constructor.
     *
     * @param beatMonitor    beat monitor
     * @param taskMonitor    task monitor
     * @param misfireMonitor misfire monitor
     */
    @Autowired
    public SimpleScheduler(BeatMonitor beatMonitor, TaskMonitor taskMonitor,
                           MisfireMonitor misfireMonitor) {
        this.beatMonitor = beatMonitor;
        this.taskMonitor = taskMonitor;
        this.misfireMonitor = misfireMonitor;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void startup() {
        beatMonitor.maintain();
        taskMonitor.maintain();
        misfireMonitor.maintain();
    }

    @Override
    public void shutdown() {
        beatMonitor.stop();
        taskMonitor.stop();
        misfireMonitor.stop();
    }
}
