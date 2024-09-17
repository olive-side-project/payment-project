package com.module.common.enumCode;

public enum PaymentErrorCode {
    IO_ERROR("IO_ERROR", "결제 요청 중 IO 오류가 발생했습니다."),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "결제 확인 중 알 수 없는 오류가 발생했습니다."),
    PAYMENT_ALREADY_COMPLETED("PAYMENT_ALREADY_COMPLETED", "결제가 이미 완료되었습니다."),
    PAYMENT_PROCESSING_IN_PROGRESS("PAYMENT_PROCESSING_IN_PROGRESS", "결제 처리 중입니다."),
    PAYMENT_PROCESSING_FAILED("PAYMENT_PROCESSING_FAILED", "결제 처리에 실패했습니다."),
    SAVE_PAYMENT_INFO_FAILED("SAVE_PAYMENT_INFO_FAILED", "결제 정보를 저장하는 데 실패했습니다"),
    INVALID_AMOUNT("INVALID_AMOUNT", "결제 금액이 유효하지 않습니다."),
    AMOUNT_MISMATCH("AMOUNT_MISMATCH", "결제 요청정보 금액과 일치하지 않습니다.");

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
