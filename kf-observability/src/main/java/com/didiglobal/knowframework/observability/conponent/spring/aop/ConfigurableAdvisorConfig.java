package com.didiglobal.knowframework.observability.conponent.spring.aop;

import com.didiglobal.knowframework.observability.common.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ConfigurableAdvisorConfig {

    private static final String PROPERTIES_KEY_POINT_CUT = "pointcut";
    private static final String POINT_CUT_DEFAULT_NAME = "execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..))";

    @Bean
    public AspectJExpressionPointcutAdvisor configurabledvisor() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(getPointcut());
        advisor.setAdvice(new LogAdvice());
        return advisor;
    }

    private String getPointcut() {
        Properties properties = PropertiesUtil.getProperties();
        /*
         * load applicationName
         */
        String pointcut = properties.getProperty(PROPERTIES_KEY_POINT_CUT);
        if(StringUtils.isBlank(pointcut)) {
            pointcut = POINT_CUT_DEFAULT_NAME;
        }
        return pointcut;
    }

}
