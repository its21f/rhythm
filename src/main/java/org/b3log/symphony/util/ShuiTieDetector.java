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

import java.util.regex.*;

public class ShuiTieDetector {
    private static final String[] KEYWORDS = {
            "顶", "沙发", "前排", "路过", "哈哈", "哈哈哈", "666", "赞", "支持", "up", "upup", "UP", "Up", "+1", "111", "来了", "留名", "占楼", "mark", "test", "测试",
            "喝水", "水贴", "阅", "围观", "签到", "打卡", "来了老弟", "来了老妹", "水水水", "水一贴", "水一水", "纯水", "纯属路过", "纯属围观", "纯属打卡", "纯属签到"
    };
    private static final Pattern REPEAT_CHAR_PATTERN = Pattern.compile("^(.)\\1{2,}$"); // 如"哈哈哈", "1111"
    private static final Pattern REPEAT_WORD_PATTERN = Pattern.compile("^(.{1,3})\\1{2,}$"); // 如"水贴水贴水贴"
    private static final Pattern ONLY_PUNCTUATION_PATTERN = Pattern.compile("^[\\p{Punct}\\s]+$"); // 只标点和空格
    private static final Pattern ONLY_EMOJI_PATTERN = Pattern.compile("^(?:[\\p{So}\\p{Cn}]+|:[a-z_]+:)+$"); // 只emoji或markdown表情
    private static final Pattern ONLY_SINGLE_WORD_PATTERN = Pattern.compile("^[喝水阅]{1,3}$"); // 只包含“喝”“水”“阅”
    public static boolean isShuiTie(String content) {
        String text = content.trim();
        // 1. 只单字水贴
        if (text.length() <= 2 && ONLY_SINGLE_WORD_PATTERN.matcher(text).matches()) return true;
        // 2. 只标点
        if (ONLY_PUNCTUATION_PATTERN.matcher(text).matches()) return true;
        // 3. 只emoji
        if (ONLY_EMOJI_PATTERN.matcher(text).matches()) return true;
        // 4. 重复字符
        if (REPEAT_CHAR_PATTERN.matcher(text).matches()) return true;
        // 5. 重复短语
        if (REPEAT_WORD_PATTERN.matcher(text).matches()) return true;
        // 6. 关键词判定只对极短内容生效
        if (text.length() <= 4) {
            for (String kw : KEYWORDS) {
                if (text.equalsIgnoreCase(kw)) return true;
            }
        }
        // 7. 长度极短且无实际内容
        int chineseCount = text.replaceAll("[^\u4e00-\u9fa5]", "").length();
        if (chineseCount == 0 && text.length() < 6) return true;
        return false;
    }
}
