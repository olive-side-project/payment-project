package com.payment.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

/**
 * 레디스에서 세션 정보를 저장, 조회 및 삭제합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionStorage {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public AuthenticationUser getSession(String token, Duration duration) {
        String session = redisTemplate.opsForValue().get(token);

        if (StringUtils.isNotEmpty(session)) {
            // 세션 값이 존재할 때만 expire 설정
            redisTemplate.expire(token, duration);
            try {
                return objectMapper.readValue(session, AuthenticationUser.class);
            } catch (Exception e) {
                log.error("session parsing error {}", session, e);
            }
        }

        return null;
    }

    public void setSession(String jwt, AuthenticationUser authUser, Duration duration) {
        try {
            String sessionStr = objectMapper.writeValueAsString(authUser);
            redisTemplate.opsForValue().set(jwt, sessionStr, duration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeSession(String token) {
        if (StringUtils.isNotBlank(token)) {
            redisTemplate.delete(Collections.singletonList(token));
        }
    }
}
