package com.module.user.enumCode;

import lombok.Getter;

/**
 * 결제 상태 관리
 */
@Getter
public enum PaymentStatus {
    COMPLETED("COMPLETED", "결제가 완료되었습니다."),
    LOCKED("LOCKED", "결제 처리가 진행 중입니다."),
    FAILED("FAILED", "결제 처리에 실패했습니다.");

    private final String code;
    private final String message;

    PaymentStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static PaymentStatus of(String code) {
        for (PaymentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}
