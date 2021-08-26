package com.didiglobal.logi.security.extend;

import com.didiglobal.logi.security.properties.LogiSecurityProper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author cjm
 */
@Component
public class ResourceExtendBeanTool {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private LogiSecurityProper logiSecurityProper;

    private static final String DEFAULT_BEAN_NAME = "defaultResourceExtendImpl";

    public ResourceExtend getResourceExtendImplBean() {
        String customBeanName = logiSecurityProper.getResourceExtendBeanName();
        Object bean = null;
        try {
            bean = applicationContext.getBean(customBeanName);
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("未能找到自定义的ResourceExtend实现类的bean，使用默认bean");
            // 如果用户没有自己实现ResourceExtend接口，则用默认的
            bean = applicationContext.getBean(DEFAULT_BEAN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ResourceExtend) bean;
    }
}
