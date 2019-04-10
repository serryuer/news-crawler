package com.bishe.crawler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class PCUtil {

    private static final Logger logger = LoggerFactory.getLogger(PCUtil.class);

    private static InetAddress netAddress;

    private static String ip = null;
    private static String host = null;

    static {
        try {
            netAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("can't get net local host info");
        }
    }

    public static String getLocalIP() {
        if (ip == null) {
            try {
                Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                while (allNetInterfaces.hasMoreElements()) {
                    NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress) addresses.nextElement();
                        if (inetAddress != null
                                && inetAddress instanceof Inet4Address
                                && !inetAddress.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                                && inetAddress.getHostAddress().indexOf(":") == -1) {
                            logger.info("本机的IP = " + inetAddress.getHostAddress());
                            ip = inetAddress.getHostAddress();
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return ip;
    }

    public static String getLocalHostName() {
        return netAddress.getHostName();
    }

    public static void main(String[] args) {
        System.out.println(PCUtil.getLocalIP());
    }

}
