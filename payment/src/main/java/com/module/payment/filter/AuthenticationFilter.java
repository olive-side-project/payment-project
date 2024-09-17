//package com.module.payment.filter;
//
//import com.module.user.auth.AuthenticationUser;
//import com.module.user.auth.SessionService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
///**
// * 인증 토큰을 검증하고 사용자 정보를 API 요청데이터에 추가합니다.
// */
//@Slf4j
//@RequiredArgsConstructor
//@Component
//@Order(2)
//public class AuthenticationFilter extends OncePerRequestFilter {
//    private final SessionService sessionService;
//
//    @Value("${app.auth.header:X-AUTH-TOKEN}")
//    private String tokenHeaderName;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String token = request.getHeader(tokenHeaderName);
//
//        if (StringUtils.isNotEmpty(token)) {
//            AuthenticationUser authUser = sessionService.getAuthUser(token);
//            if (authUser != null) {
//                request.setAttribute("authUser", authUser);
//                request.setAttribute("authToken", token);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
