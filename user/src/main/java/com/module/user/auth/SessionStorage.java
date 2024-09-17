package com.module.user.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.common.AuthenticationUser;
import com.module.common.exception.PaymentException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
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
            log.debug(e.toString());
            throw new PaymentException("SESSION_ERROR", "Failed to set session in Redis", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void removeSession(String token) {
        if (StringUtils.isNotBlank(token)) {
            redisTemplate.delete(Collections.singletonList(token));
        }
    }
}
