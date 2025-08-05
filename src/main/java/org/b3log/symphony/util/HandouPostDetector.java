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

public class HandouPostDetector {
    // 支持 :white_large_square:、⬜️、🟩、🟧、🟡、🟠
    private static final String EMOJI_REGEX = "(?::white_large_square:|⬜️|🟩|🟧|🟡|🟠)";
    private static final Pattern EMOJI_LINE_PATTERN = Pattern.compile("^(" + EMOJI_REGEX + "){4}$", Pattern.MULTILINE);
    private static final Pattern GREEN_LINE_PATTERN = Pattern.compile("^(🟩){4}$", Pattern.MULTILINE);
    public static boolean isHandouPost(String content) {
        Matcher emojiMatcher = EMOJI_LINE_PATTERN.matcher(content);
        boolean hasEmojiLine = false;
        String lastEmojiLine = null;
        while (emojiMatcher.find()) {
            hasEmojiLine = true;
            lastEmojiLine = emojiMatcher.group();
        }
        if (!hasEmojiLine) return false;
        // 检查最后一行是否全绿
        if (lastEmojiLine != null && lastEmojiLine.equals("🟩🟩🟩🟩")) {
            return true;
        }
        // 也允许只有一行且是全绿
        Matcher greenMatcher = GREEN_LINE_PATTERN.matcher(content);
        if (greenMatcher.find() && !emojiMatcher.find()) {
            return true;
        }
        return false;
    }
}
