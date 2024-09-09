package com.payment.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 토스 페이먼츠 API 통신에 필요한 설정 정보를 관리합니다.
 */
@Data
@Component
public class PaymentProperties {
    @Value("${toss.payment.base-url}")
    private String tossPaymentBaseUrl;

    @Value("${toss.payment.confirm-endpoint}")
    private String tossPaymentConfirmEndpoint;

    @Value("${toss.payment.secret-key}")
    private String tossPaymentSecretKey;

    @Value("${toss.payment.connection-timeout:3000}")
    private int connectionTimeout;

    @Value("${toss.payment.read-timeout:5000}")
    private int readTimeout;
}
