package com.payment.filter;

import com.payment.http.RequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 이 필터는 주로 HTTP 요청의 내용을 복사하거나 그대로 전달하는 역할
 * 특정한 상황에서는 예외적으로 다르게 처리하며, 이때 HTTP 요청의 내용을 따로 저장하지 않음
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class RequestBodyCopyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExceptionalPath(request)) {
            filterChain.doFilter(request, response);
        } else {
            // 요청 본문 복사
            RequestWrapper requestWrapper = new RequestWrapper(request);
            filterChain.doFilter(requestWrapper, response);
        }
    }

    private boolean isExceptionalPath(HttpServletRequest request) {
        String path = request.getServletPath();
        return StringUtils.startsWith(path, "/swagger") ||
                StringUtils.startsWith(path, "/swagger-ui") ||
                StringUtils.startsWith(path, "/actuator/") ||
                StringUtils.startsWith(path, "/webjars") ||
                StringUtils.startsWith(path, "/v3/api-docs");
    }
}
