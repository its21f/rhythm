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
