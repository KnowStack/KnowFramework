package com.didiglobal.logi.security.util;

import org.springframework.util.DigestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cjm
 */
public class MathUtil {

    public static void main(String[] args) {
        String passwd = "12345" + "123456";
        String md5Password = DigestUtils.md5DigestAsHex(passwd.getBytes());
        System.out.println(md5Password.length());
    }

    /**
     * 随机获取整数
     * @param len 长度
     * @return long
     */
    public static long getRandomNumber(int len) {
        if(len <= 0 || len > 18) {
            return 0;
        }
        return (long) ((Math.random() + 1) * Math.pow(10, len));
    }

    /**
     * 求两个数组的交集
     *
     * @param list1 数组1
     * @param list2 数组2
     * @return 交集元素
     */
    public static Set<Integer> getIntersection(List<Integer> list1, List<Integer> list2) {
        Set<Integer> result = new HashSet<>();
        Set<Integer> set = new HashSet<>(list2);
        for (Integer num : list1) {
            if (set.contains(num)) {
                result.add(num);
            }
        }
        return result;
    }
}
