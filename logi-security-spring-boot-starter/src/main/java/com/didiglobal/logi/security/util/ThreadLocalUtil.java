package com.didiglobal.logi.security.util;

/**
 * @author jmcai
 * @version 1.0
 * @date 2021/1/26 12:55
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Integer info) {
        THREAD_LOCAL.set(info);
    }
    
    public static Integer get() {
        Integer i = THREAD_LOCAL.get();
        if(i == null) {
            return 1;
        }
        return i;
    }
    
    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
