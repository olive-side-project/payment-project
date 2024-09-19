package com.module.payment.dto.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPaymentsConfirmRequest {
    @NotNull
    @Schema(description = "가맹점 코드, 10자")
    private String cid;

    @Schema(description = "가맹점 코드 인증키, 24자, 숫자+영문 소문자 조합")
    private String cidSecret;

    @NotNull
    @Schema(description = "결제 고유번호, 결제 준비 API 응답에 포함")
    private String tid;

    @NotNull
    @Schema(description = "가맹점 주문번호, 결제 준비 API 요청과 일치해야 함")
    private String partnerOrderId;

    @NotNull
    @Schema(description = "가맹점 회원 id, 결제 준비 API 요청과 일치해야 함")
    private String partnerUserId;

    @NotNull
    @Schema(description = "결제승인 요청을 인증하는 토큰")
    private String pgToken;

    @Schema(description = "결제 승인 요청에 대해 저장하고 싶은 값, 최대 200자")
    private String payload;

    @Min(0)
    @Schema(description = "상품 총액, 결제 준비 API 요청과 일치해야 함")
    private Long totalAmount;
}
