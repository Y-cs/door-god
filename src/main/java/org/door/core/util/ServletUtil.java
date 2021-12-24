package org.door.core.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 11:20
 * @Description:
 **/
public class ServletUtil {


    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        if (request != null) {
            // 获取用户真是的地址
            String xip = request.getHeader("X-Real-IP");
            // 获取多次代理后的IP字符串值
            String xFor = request.getHeader("X-Forwarded-For");
            if (xFor != null && xFor.length() > 0 && !"unknown".equalsIgnoreCase(xFor)) {
                // 多次反向代理后会有多个IP值，第一个用户真实的IP地址
                int index = xFor.indexOf(",");
                if (index >= 0) {
                    return xFor.substring(0, index);
                } else {
                    return xFor;
                }
            }
            ip = xip;
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }
        }
        return ip;
    }
}
