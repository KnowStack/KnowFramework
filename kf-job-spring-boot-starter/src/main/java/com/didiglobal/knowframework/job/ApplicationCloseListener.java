package com.didiglobal.knowframework.job;

import com.didiglobal.knowframework.job.core.Scheduler;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

@Service
public class ApplicationCloseListener implements ApplicationListener<ContextClosedEvent> {

    private static final ILog logger     = LogFactory.getLog(ApplicationCloseListener.class);

    private ApplicationContext applicationContext;

    @Autowired
    public ApplicationCloseListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.error("class=ApplicationCloseListener||method=onApplicationEvent||url=||"
                + "msg=shutdown auv job!!!");
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        scheduler.shutdown();
    }
}