package com.didiglobal.knowframework.observability.common.util;

import com.didiglobal.knowframework.observability.Observability;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final String PROPERTIES_FILE_NAME = "/observability.properties";
    private static Properties properties = null;

    public static synchronized Properties getProperties() {
        if(null == properties) {
            properties = loadProperties();
        }
        return properties;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = Observability.class.getResourceAsStream(PROPERTIES_FILE_NAME);
            properties.load(is);
        } catch (Exception ex) {
            //未加载成功或对应文件不存在，采用默认值
        } finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
        return properties;
    }

}
