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
