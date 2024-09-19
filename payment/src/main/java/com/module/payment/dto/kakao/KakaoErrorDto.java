package com.module.payment.dto.kakao;

import com.module.payment.dto.ApiError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoErrorDto implements ApiError {
    private int errorCode;
    private String errorMessage;
    private Extras extras;

    @Override
    public String getCode() {
        return String.valueOf(errorCode);
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Extras {
        private String methodResultCode;
        private String methodResultMessage;
    }
}