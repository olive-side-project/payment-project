package com.payment.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 레디스에 저장된 세션 정보를 관리하고, 토큰의 유효성을 검증합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService {
    private final SessionStorage sessionStorage;
    private final JwtService jwtService;

    @Value("${app.session.expire:1h}")
    private Duration sessionTime;

    public AuthenticationUser getAuthUser(String token) {
        long userSeqFromJwt = jwtService.getUserSeqFromJWT(token);
        AuthenticationUser authUser = sessionStorage.getSession(token, sessionTime);

        if (authUser != null && userSeqFromJwt != authUser.getUserSeq()) {
            log.error("session error different user {} {}", userSeqFromJwt, authUser.getUserSeq());
            throw new RuntimeException("InvalidToken");
        }

        return authUser;
    }

    // 주어진 토큰에 사용자 정보를 설정하고, 세션 시간 적용
    public void setAuthUser(String jwt, AuthenticationUser authUser) {
        sessionStorage.setSession(jwt, authUser, sessionTime);
    }

    // 주어진 토큰에 연결된 사용자 정보를 제거
    public void removeAuthUser(String token) {
        sessionStorage.removeSession(token);
    }
}
