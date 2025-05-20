package org.b3log.symphony.util;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class OpenIdUtil {

    private static final String SECRET = Symphonys.get("openid.secret");
    public static String generateNonce() {
        // 时间部分
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = sdf.format(new Date());

        // 随机部分（你也可以用更短的 UUID 或随机数）
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        return timestamp + random;
    }

    public static String sign(Map<String, String> fields) throws Exception{
        StringBuilder sb = new StringBuilder();
        String[] signedFields = fields.get("openid.signed").split(",");
        for (String field : signedFields) {
            String key = "openid." + field;
            sb.append(key).append(":").append(fields.get(key)).append("\n");
        }

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hash = sha1.digest((sb.toString() + SECRET).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }


    public static Date extractNonceTimestamp(String nonce) throws ParseException {
        if (nonce.length() < 20) {
            throw new IllegalArgumentException("Invalid nonce format");
        }

        String timestampPart = nonce.substring(0, 20); // "2025-05-14T09:42:18Z"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(timestampPart);
    }
}
