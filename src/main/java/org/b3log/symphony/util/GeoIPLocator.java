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

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Subdivision;
import org.json.JSONObject;
import java.io.File;
import java.net.InetAddress;
import java.util.List;

public class GeoIPLocator {
    private static volatile GeoIPLocator instance;
    private DatabaseReader reader;
    private GeoIPLocator(String dbPath) throws Exception {
        reader = new DatabaseReader.Builder(new File(dbPath)).build();
    }
    // 单例获取
    public static GeoIPLocator getInstance(String dbPath) throws Exception {
        if (instance == null) {
            synchronized (GeoIPLocator.class) {
                if (instance == null) {
                    instance = new GeoIPLocator(dbPath);
                }
            }
        }
        return instance;
    }
    /**
     * 查询IP的省市信息
     * @param ip IPv4字符串
     * @return JSONObject {country, province, city}，查不到返回null
     */
    public JSONObject getLocation(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = reader.city(ipAddress);

            // 国家
            String country = response.getCountry().getNames().get("zh-CN");
            if (country == null) {
                country = response.getCountry().getName(); // 英文名兜底
            }

            // 省份/州
            List<Subdivision> subdivisions = response.getSubdivisions();
            String province = null;
            if (subdivisions.size() > 0) {
                province = subdivisions.get(0).getNames().get("zh-CN");
                if (province == null) {
                    province = subdivisions.get(0).getName(); // 英文名兜底
                }
            }

            // 城市
            City cityObj = response.getCity();
            String city = cityObj.getNames().get("zh-CN");
            if (city == null) {
                city = cityObj.getName(); // 英文名兜底
            }

            // 只要有国家就返回
            if (country == null) {
                return null;
            }

            if (city != null) {
                city = city.replace("市", "");
            }

            JSONObject ret = new JSONObject();
            ret.put("country", country);
            if (province != null) ret.put("province", province);
            if (city != null) ret.put("city", city);

            return ret;
        } catch (Exception e) {
            // 可加日志
            return null;
        }
    }

    // 可选：关闭数据库资源
    public void close() {
        try {
            if (reader != null) reader.close();
        } catch (Exception ignored) {}
    }
}