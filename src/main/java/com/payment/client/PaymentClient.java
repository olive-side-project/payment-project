package com.payment.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.config.PaymentProperties;
import com.payment.dto.TossPaymentDto.PaymentRequest;
import com.payment.dto.TossPaymentDto.PaymentResponse;

import com.payment.exception.PaymentException;
import com.payment.interceptor.PaymentExceptionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * 토스 페이먼츠 API 와의 통신을 담당합니다.
 */
@Component
@Slf4j
public class PaymentClient {

    private final PaymentProperties paymentProperties;
    private final RestTemplate restTemplate;
    private final PaymentClientConfig paymentClientConfig;

    public PaymentClient(PaymentProperties paymentProperties, PaymentClientConfig paymentClientConfig) {
        this.paymentProperties = paymentProperties;
        this.paymentClientConfig = paymentClientConfig;
        this.restTemplate = new RestTemplate(createPaymentRequestFactory());
        this.restTemplate.getInterceptors().add(new PaymentExceptionInterceptor());
    }

    // 결제 API 요청을 위한 ClientHttpRequestFactory 생성
    private ClientHttpRequestFactory createPaymentRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(paymentProperties.getConnectionTimeout());
        factory.setReadTimeout(paymentProperties.getReadTimeout());

        return factory;
    }

    // 결제 확인 요청 API 호출
    public PaymentResponse confirmPayments(PaymentRequest paymentRequest) {
        String url = buildPaymentConfirmUrl();
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, paymentClientConfig.createHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            log.info("Response Body: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(responseBody, PaymentResponse.class);
            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 중 오류 발생: {}", e.getMessage());
                throw new PaymentException("UNKNOWN_ERROR", "결제 확인 중 JSON 파싱 오류가 발생했습니다.", e);
            }
        } catch (HttpClientErrorException e) {
            throw paymentClientConfig.handleClientErrorException(e);
        }
    }

    private String buildPaymentConfirmUrl() {
        return String.format("%s%s",
                paymentProperties.getTossPaymentBaseUrl(),
                paymentProperties.getTossPaymentConfirmEndpoint());
    }
}
