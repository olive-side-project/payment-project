package com.payment.exception;

import com.payment.enumCode.PaymentErrorCode;

/**
 * 결제 관련 오류를 나타내는 사용자 정의 예외입니다.
 */
public class PaymentException extends RuntimeException {
    private final String code;

    public PaymentException(PaymentErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }

    public PaymentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }

    public String getCode() {
        return code;
    }
}
