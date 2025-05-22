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

import com.qiniu.common.Constants;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class QiniuTextCensor {

    private static HashMap<String, JSONObject> cache = new HashMap<>();

    public static JSONObject censor(String text) {
        try {
            String md5 = convertToMD5(text);
            if (cache.containsKey(md5)) {
                return cache.get(md5);
            }
            Auth auth = Auth.create(Symphonys.UPLOAD_QINIU_AK, Symphonys.UPLOAD_QINIU_SK);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", new JSONObject().put("text", text));
            jsonObject.put("params", new JSONObject("{\"scenes\": [\"antispam\"]}"));
            byte[] body = jsonObject.toString().getBytes(Constants.UTF_8);
            String url = "https://ai.qiniuapi.com/v3/text/censor";
            StringMap headers = auth.authorizationV2(url, "POST", body, "application/json");
            Response response = new Client().post(url, body, headers, "application/json");
            JSONObject result = new JSONObject(response.bodyString());
            JSONObject retResult = new JSONObject();
            retResult.put("action", result.optJSONObject("result").optString("suggestion"));
            String type = result.optJSONObject("result").optJSONObject("scenes").optJSONObject("antispam").optJSONArray("details").optJSONObject(0).optString("label");
            switch (type) {
                case "normal":
                    retResult.put("type", "正常");
                    break;
                case "spam":
                    retResult.put("type", "垃圾信息");
                    break;
                case "ad":
                    retResult.put("type", "广告");
                    break;
                case "politics":
                    retResult.put("type", "涉政");
                    break;
                case "terrorism":
                    retResult.put("type", "暴恐");
                    break;
                case "abuse":
                    retResult.put("type", "辱骂");
                    break;
                case "porn":
                    retResult.put("type", "色情");
                    break;
                case "flood":
                    retResult.put("type", "灌水");
                    break;
                case "contraband":
                    retResult.put("type", "违禁");
                    break;
                case "meaningless":
                    retResult.put("type", "无意义");
                    break;
            }
            // 如果是涉政、暴恐、色情、违禁
            if ("politics".equals(type) || "terrorism".equals(type) || "porn".equals(type) || "contraband".equals(type)) {
                retResult.put("do", "block");
            } else {
                retResult.put("do", "pass");
            }
            cache.put(md5, retResult);
            System.out.println(retResult);
            return retResult;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("do", "pass").put("action", "review").put("type", "未知");
        }
    }

    public static String convertToMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] inputBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : inputBytes) {
                sb.append(String.format("%02x", b));
            }
            String md5 = sb.toString();
            return md5;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
