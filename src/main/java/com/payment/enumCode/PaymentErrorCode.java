package com.payment.enumCode;

public enum PaymentErrorCode {
    IO_ERROR("IO_ERROR", "결제 요청 중 IO 오류가 발생했습니다."),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "결제 확인 중 알 수 없는 오류가 발생했습니다.");

    private final String code;
    private final String message;

    PaymentErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
