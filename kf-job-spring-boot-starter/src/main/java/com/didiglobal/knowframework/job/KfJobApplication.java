package com.didiglobal.knowframework.job;

import com.didiglobal.knowframework.job.core.monitor.BeatMonitor;
import com.didiglobal.knowframework.job.core.monitor.MisfireMonitor;
import com.didiglobal.knowframework.job.core.monitor.TaskMonitor;
import com.didiglobal.knowframework.job.core.SimpleScheduler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author didi
 */
@Configuration
@ComponentScan(basePackages = "com.didiglobal.knowframework.job")
public class KfJobApplication {

    /**
     * 入口函数.
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(KfJobApplication.class);
        BeatMonitor beatMonitor = applicationContext.getBean(BeatMonitor.class);
        TaskMonitor taskMonitor = applicationContext.getBean(TaskMonitor.class);
        MisfireMonitor misfireMonitor = applicationContext.getBean(MisfireMonitor.class);
        SimpleScheduler simpleScheduler = new SimpleScheduler(beatMonitor, taskMonitor, misfireMonitor);
        simpleScheduler.startup();
    }
}
