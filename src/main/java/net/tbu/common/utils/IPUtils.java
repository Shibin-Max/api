package net.tbu.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public abstract class IPUtils {

    public static String getRequestIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
//        logger.debug("1. 请求的ip:{}", ip);
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
//        logger.info("获取请求的ip:{}", ip);
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            String[] ips = ip.split(",");
            ip = ips[0];
//            logger.info("截取后的ip为:{}", ip);
        }
        return ip;
    }
}
