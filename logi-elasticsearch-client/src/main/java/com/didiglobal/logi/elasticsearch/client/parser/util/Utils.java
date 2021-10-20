package com.didiglobal.logi.elasticsearch.client.parser.util;


import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class Utils {


    public static String logExceptionStack(Throwable e) {
        StringWriter errorsWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(errorsWriter));
        return getPrefix(errorsWriter.toString());
    }


    public static String getMD5WithVersion(String str, String version) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes("utf-8"));
            return version.concat(toHex(bytes));
        } catch (Exception e) {
            return "";
        }
    }


    public static String getPrefix(String str) {
        if (null == str) {
            return "";
        }
        String content = str.length() > 4096 ? str.substring(0, 4095) + "..." : str;

        if (content.contains("\n")) {

            int first = content.indexOf("\n");
            int end = content.lastIndexOf("\n");
            if (first == end) {
                content = content.replaceAll("\r|\n*", " ");
            } else {
                content = content.replaceAll("[\\t\\n\\r]", " ");
            }
        }
        return content;
    }


    public static void addSetItem(Set<String> set, String item) {
        if (StringUtils.isNotBlank(item)) {
            set.add(item);
        }
    }


    public static void addSetItemWithCommSplit(Set<String> set, String items) {
        if (StringUtils.isNotBlank(items)) {
            for (String item : StringUtils.splitByWholeSeparatorPreserveAllTokens(items, ",")) {
                set.add(item);
            }
        }
    }


    public static String trimEndComma(String str) {
        if (StringUtils.isNotBlank(str) && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() -1);
        }
        return str;
    }


    public static Long getMaxLong(List<Long> longList) {
        if (longList == null || longList.size() == 0) {
            return 0L;
        }
        return Collections.max(longList);
    }


    public static String getFirst(List<String> strList) {
        if (strList == null || strList.size() == 0) {
            return "";
        }
        return strList.get(0);
    }


    private static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

}
