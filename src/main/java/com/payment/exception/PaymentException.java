package com.payment.exception;

import com.payment.enumCode.PaymentErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 결제 관련 오류를 나타내는 사용자 정의 예외입니다.
 */
@Getter
@Builder(builderMethodName = "hiddenBuilder")
public class PaymentException extends RuntimeException {
    private final String code;
    private final String message;
    private final HttpStatus statusCode;

    // 기존 생성자들
    public PaymentException(PaymentErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.statusCode = HttpStatus.BAD_REQUEST;
    }

    public PaymentException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.statusCode = HttpStatus.BAD_REQUEST;
    }

    public PaymentException(String code, String message, HttpStatus statusCode) {
        super(message);
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public PaymentException(String message, HttpStatus statusCode) {
        super(message);
        this.code = message;
        this.message = message;
        this.statusCode = statusCode;
    }

    // code와 statusCode만 필요한 경우를 위한 빌더 메서드
    public static PaymentExceptionBuilder builder(String code, HttpStatus statusCode) {
        return hiddenBuilder().code(code).statusCode(statusCode).message(""); // 메시지 없음
    }
}
