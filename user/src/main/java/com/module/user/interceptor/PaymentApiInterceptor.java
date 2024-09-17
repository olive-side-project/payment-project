package com.module.user.interceptor;

import com.module.user.annotation.Public;
import com.module.user.auth.AuthenticationUser;
import com.module.user.exception.PaymentException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 권한 인터셉터
 * - 필터 이후, 컨트롤러 요청 전 인증 및 로그인 완료 여부를 체크합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentApiInterceptor implements HandlerInterceptor {
    private static final String[] PUBLIC_PATHS = {
            "/v3/api-docs/",
            "/swagger-ui/",
            "/swagger-resources/",
            "/webjars/"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        for (String path : PUBLIC_PATHS) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            // @Public 이 붙은 메서드나 클래스는 인증 및 권한 체크를 생략
            if (isPublicMethod(method)) {
                return true;
            }

            // 현재 인증된 사용자 정보
            AuthenticationUser authUser = getAuthenticationUser(request);

            // 로그인 완료 여부 확인
            validateLoginStatus(authUser);
        }

        return true;
    }

    private boolean isPublicMethod(HandlerMethod method) {
        return method.getBean().getClass().isAnnotationPresent(Public.class)
                || method.getMethod().isAnnotationPresent(Public.class);
    }

    private AuthenticationUser getAuthenticationUser(HttpServletRequest request) {
        AuthenticationUser authUser = (AuthenticationUser) request.getAttribute("authUser");
        if (authUser == null) {
            throw new PaymentException("UNAUTHORIZED", "인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
        }

        return authUser;
    }

    private void validateLoginStatus(AuthenticationUser authUser) {
        if (!authUser.isLoginComplete()) {
            throw new PaymentException("FORBIDDEN", "로그인이 필요합니다.", HttpStatus.FORBIDDEN);
        }
    }
}
