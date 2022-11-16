package com.didiglobal.logi.observability.conponent.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ApplicationMybatisInjectionListener implements ApplicationListener<ContextRefreshedEvent> {

    private volatile AtomicBoolean isInit = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //防止重复触发
        if(!isInit.compareAndSet(false,true)) {
            return;
        }
        String[] nameArray = contextRefreshedEvent.getApplicationContext().getBeanNamesForType(SqlSessionFactory.class);
        for (String name : nameArray) {
            SqlSessionFactory sqlSessionFactory = contextRefreshedEvent.getApplicationContext().getBean(name, SqlSessionFactory.class);
            sqlSessionFactory.getConfiguration().addInterceptor(new ObservabilityInterceptor());
        }
    }

}
