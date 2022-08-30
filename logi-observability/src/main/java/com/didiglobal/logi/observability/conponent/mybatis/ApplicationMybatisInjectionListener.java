package com.didiglobal.logi.observability.conponent.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class ApplicationMybatisInjectionListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        SqlSessionFactory sqlSessionFactory = contextRefreshedEvent.getApplicationContext().getBean(SqlSessionFactory.class);
        sqlSessionFactory.getConfiguration().addInterceptor(new ObservabilityInterceptor());
    }

}
