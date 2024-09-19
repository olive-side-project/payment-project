package com.module.payment.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.payment.config.PaymentProperties;
import com.module.payment.dto.toss.TossPaymentsConfirmRequest;
import com.module.payment.dto.toss.TossPaymentsConfirmResponse;
import com.module.payment.dto.toss.TossPaymentsTransactionResponse;
import com.module.payment.dto.toss.TossErrorDto;
import com.module.common.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Toss API와의 통신을 담당합니다.
 */
@Slf4j
@Component
public class TossPaymentClient implements PaymentClient<TossPaymentsConfirmResponse, TossPaymentsConfirmRequest> {
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";

    private final PaymentProperties paymentProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;

    public TossPaymentClient(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
        this.restClient = createRestClient();
    }

    private RestClient createRestClient() {
        return RestClient.builder()
                .baseUrl(paymentProperties.getTossPaymentBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    @Override
    public TossPaymentsConfirmResponse confirmPayments(TossPaymentsConfirmRequest confirmRequest) {
        String url = buildPaymentConfirmUrl();

        try {
            HttpHeaders headers = createHeaders();
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(confirmRequest)
                    .retrieve()
                    .toEntity(String.class);

            log.info("Response Body: {}", responseEntity.getBody());
            return parseJson(responseEntity.getBody(), TossPaymentsConfirmResponse.class);
        } catch (Exception e) {
            log.error("결제 확인 API 오류", e);
            if (e instanceof RestClientResponseException) {
                handleErrorResponse(((RestClientResponseException) e).getResponseBodyAsString());
            }
            throw new PaymentException("UNKNOWN_ERROR", "결제 확인 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<TossPaymentsTransactionResponse> getTransactions(String startDate, String endDate) {
        String url = buildTransactionListUrl(startDate, endDate);

        try {
            HttpHeaders headers = createHeaders();
            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .toEntity(String.class);

            log.info("Response Body: {}", responseEntity.getBody());
            return parseJsonArray(responseEntity.getBody());
        } catch (RestClientResponseException e) {
            log.error("RestClientResponseException - Status Code: {}, Response Body: {}",
                    e.getRawStatusCode(), e.getResponseBodyAsString());
            handleErrorResponse(e.getResponseBodyAsString());
            throw new PaymentException("API_ERROR", "거래 목록 조회 중 API 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("거래 목록 API 처리 중 알 수 없는 오류가 발생했습니다.", e);
            throw new PaymentException("UNKNOWN_ERROR", "거래 목록 조회 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    private String buildPaymentConfirmUrl() {
        return String.format("%s%s",
                paymentProperties.getTossPaymentBaseUrl(),
                paymentProperties.getTossPaymentConfirmEndpoint());
    }

    private String buildTransactionListUrl(String startDate, String endDate) {
        return String.format("%s%s?startDate=%s&endDate=%s",
                paymentProperties.getTossPaymentBaseUrl(),
                paymentProperties.getTossPaymentTransactionEndpoint(),
                startDate,
                endDate);
    }

    private void handleErrorResponse(String responseBody) {
        try {
            TossErrorDto errorDto = parseJson(responseBody, TossErrorDto.class);
            throw new PaymentException(errorDto.getCode(), HttpStatus.valueOf(errorDto.getMessage()));
        } catch (JsonProcessingException e) {
            log.error("오류 응답 JSON 파싱 중 오류 발생: {}", e.getMessage());
            throw new PaymentException("UNKNOWN_ERROR", "오류 응답 JSON 파싱 중 오류가 발생했습니다.", e);
        }
    }

    private <T> T parseJson(String jsonString, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, valueType);
    }

    private <T> List<T> parseJsonArray(String jsonString) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        if (jsonNode.isArray()) {
            return objectMapper.readValue(jsonNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TossPaymentsTransactionResponse.class));
        } else {
            return Collections.emptyList();
        }
    }

    private String createPaymentAuthHeader() {
        String credentials = paymentProperties.getTossPaymentSecretKey() + BASIC_DELIMITER;
        byte[] encodedBytes = Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8));
        return AUTH_HEADER_PREFIX + new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
