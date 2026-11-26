package com.sleep.system.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 轻量 JWT 实现（HS256），避免引入额外依赖
 */
public final class JwtUtil {

    private static final String SECRET = "sleep-system-jwt-secret-2026-v1";

    private static final long EXPIRE_MILLIS = 7L * 24 * 60 * 60 * 1000;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private JwtUtil() {
    }

    public record Claims(Long subjectId, String type, long expireAt) {
    }

    public static String create(Long subjectId, String type) {
        try {
            String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", subjectId);
            payload.put("type", type);
            payload.put("iat", System.currentTimeMillis());
            payload.put("exp", System.currentTimeMillis() + EXPIRE_MILLIS);
            String body = encode(MAPPER.writeValueAsString(payload));
            String content = header + "." + body;
            return content + "." + sign(content);
        } catch (Exception exception) {
            throw new BusinessException(500, "令牌生成失败");
        }
    }

    @SuppressWarnings("unchecked")
    public static Claims parse(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(401, "请先登录");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        String content = parts[0] + "." + parts[1];
        if (!sign(content).equals(parts[2])) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        try {
            String json = new String(DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = MAPPER.readValue(json, Map.class);
            long expireAt = ((Number) payload.get("exp")).longValue();
            if (expireAt < System.currentTimeMillis()) {
                throw new BusinessException(401, "登录已过期，请重新登录");
            }
            Long subjectId = ((Number) payload.get("sub")).longValue();
            String type = String.valueOf(payload.get("type"));
            return new Claims(subjectId, type, expireAt);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
    }

    private static String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new BusinessException(500, "令牌签名失败");
        }
    }
}
