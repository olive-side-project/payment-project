package com.payment.exception;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * 예외를 전역적으로 처리하는 핸들러입니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponse> buildErrorResponse(String code, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        return buildErrorResponse("REDIS_CONNECTION_FAILURE", "Redis 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        return buildErrorResponse(e.getCode(), e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();
        String message = e.getResponseBodyAsString();
        return buildErrorResponse("HTTP_CLIENT_ERROR", message, status);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();
        String message = e.getResponseBodyAsString();
        if (message.isEmpty()) {
            message = "서버 내부 오류가 발생했습니다.";
        }
        return buildErrorResponse("HTTP_SERVER_ERROR", message, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        return buildErrorResponse("UNKNOWN_ERROR", "서버에서 알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class ErrorResponse {
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = formatMessage(message);
        }

        private String formatMessage(String message) {
            return message.replaceFirst("com\\.payment\\.exception\\.PaymentException:", "").trim();
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
