package com.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LoginDto {

    @Data
    public static class LoginRequest {
        @Schema(description = "이메일", example = "newuser@domain.com")
        private String email;

        @Schema(description = "비밀번호", example = "password123!")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResponse {
        @Schema(description = "사용자 순번")
        private long userSeq;

        @Schema(description = "토큰")
        private String token;
    }

    @Data
    public static class SignUpRequest {
        @Schema(description = "이메일", example = "newuser@domain.com")
        private String email;

        @Schema(description = "비밀번호", example = "password123!")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpResponse {
        @Schema(description = "사용자 순번")
        private Long userSeq;

        @Schema(description = "회원가입 완료 메시지")
        private String message;
    }
}
