/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.util;

import java.util.regex.Pattern;

public class IPUtils {
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$"
    );
    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|"
                    + "(([0-9A-Fa-f]{1,4}:){1,7}:)|"
                    + "(([0-9A-Fa-f]{1,4}:){1,6}:[0-9A-Fa-f]{1,4})|"
                    + "(([0-9A-Fa-f]{1,4}:){1,5}(:[0-9A-Fa-f]{1,4}){1,2})|"
                    + "(([0-9A-Fa-f]{1,4}:){1,4}(:[0-9A-Fa-f]{1,4}){1,3})|"
                    + "(([0-9A-Fa-f]{1,4}:){1,3}(:[0-9A-Fa-f]{1,4}){1,4})|"
                    + "(([0-9A-Fa-f]{1,4}:){1,2}(:[0-9A-Fa-f]{1,4}){1,5})|"
                    + "([0-9A-Fa-f]{1,4}:((:[0-9A-Fa-f]{1,4}){1,6}))|"
                    + "(:((:[0-9A-Fa-f]{1,4}){1,7}|:))" // 支持简写 ::
    );

    public static String getFirstIP(String ipStr) {
        if (ipStr == null || ipStr.isEmpty()) {
            return ipStr;
        }
        String[] ips = ipStr.split(",");
        String firstIp = ips[0].trim();
        if (isIPv4(firstIp) || isIPv6(firstIp)) {
            return firstIp;
        } else {
            return ipStr;
        }
    }

    // 新增IPv6判断方法
    private static boolean isIPv6(String ip) {
        return IPV6_PATTERN.matcher(ip).matches();
    }

    public static String getFirstIPv4(String ipStr) {
        if (ipStr == null || ipStr.isEmpty()) {
            return ipStr;
        }
        // 分割
        String[] ips = ipStr.split(",");
        String firstIp = ips[0].trim();
        // 校验是否IPv4
        if (isIPv4(firstIp)) {
            return firstIp;
        } else {
            // 不是IPv4，直接返回原始字符串
            return ipStr;
        }
    }

    private static boolean isIPv4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }
}
