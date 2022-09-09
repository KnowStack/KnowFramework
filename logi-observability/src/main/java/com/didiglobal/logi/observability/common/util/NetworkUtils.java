package com.didiglobal.logi.observability.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkUtils {

    /**
     * 主机名默认值 用于无法获取对应主机名时
     */
    private static final String HOST_NAME_DEFAULT_VALUE = "LocalHost";
    /**
     * 主机 ip 默认值 用于无法获取对应主机 ip 时
     */
    private static final String HOST_IP_DEFAULT_VALUE = "127.0.0.1";

    private static final String HOST_NAME;
    private static final String HOST_IP;

    static {
        HOST_NAME = NetworkUtils.handleGetHostName();
        HOST_IP = handleGetHostIp();
    }

    public static String getHostName() {
        return HOST_NAME;
    }

    public static String getHostIp() {
        return HOST_IP;
    }

    /**
     * @return 主机名
     */
    private static String handleGetHostName() {
        try {
            String hostname = getHostnameByExec();
            if (StringUtils.isNotBlank(hostname)) {
                return hostname.trim();
            } else {
                return InetAddress.getLocalHost().getHostName();
            }
        } catch (UnknownHostException e) {
            return HOST_NAME_DEFAULT_VALUE;
        }
    }

    /**
     * @return 主机 ip
     */
    private static String handleGetHostIp() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    // 排除loopback类型地址
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                //The JDK InetAddress.getLocalHost() method unexpectedly returned default value.
                return HOST_IP_DEFAULT_VALUE;
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            //Failed to determine LAN address
            return HOST_IP_DEFAULT_VALUE;
        }
    }

    /**
     * @return 通过执行 hostname 命令获取主机名
     */
    private static String getHostnameByExec() {
        StringBuffer buf = new StringBuffer();
        try {
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("hostname");
            BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = br.readLine()) != null) {
                buf.append(s);
            }
            String hostname = buf.toString();
            if (StringUtils.isBlank(hostname) || hostname.contains("localhost")
                    || hostname.indexOf("请求超时") != -1) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return buf.toString();
    }

}
