package com.payment.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.config.PaymentProperties;
import com.payment.exception.PaymentException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * PaymentClient 인증 및 오류 처리 관련 설정을 담당합니다.
 */
@Component
public class PaymentClientConfig {

    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";

    private final PaymentProperties paymentProperties;
    private final ObjectMapper objectMapper;

    public PaymentClientConfig(PaymentProperties paymentProperties, ObjectMapper objectMapper) {
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
    }

    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader());
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    private String createPaymentAuthHeader() {
        String credentials = paymentProperties.getTossPaymentSecretKey() + BASIC_DELIMITER;
        byte[] encodedBytes = Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8));

        return AUTH_HEADER_PREFIX + new String(encodedBytes);
    }

    public PaymentException handleClientErrorException(HttpClientErrorException e) {
        String detailMessage = e.getMessage();
        try {
            if (detailMessage != null && detailMessage.contains("{")) {
                String jsonPart = detailMessage.substring(detailMessage.indexOf("{"));
                JsonNode errorNode = objectMapper.readTree(jsonPart);
                String errorCode = errorNode.get("code").asText();
                String errorMessage = errorNode.get("message").asText();

                return new PaymentException(errorCode, errorMessage, e);
            }
        } catch (Exception ex) {
            throw new PaymentException("UNKNOWN_ERROR", "결제 확인 중 알 수 없는 오류가 발생했습니다.", ex);
        }

        return new PaymentException(e.getStatusCode().toString(), e.getStatusText(), e);
    }
}
