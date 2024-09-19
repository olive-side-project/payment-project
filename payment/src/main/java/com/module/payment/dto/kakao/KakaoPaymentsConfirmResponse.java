package com.module.payment.dto.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPaymentsConfirmResponse {
    @Schema(description = "상점 ID")
    private String cid;

    @Schema(description = "고유 거래 ID")
    private String aid;

    @Schema(description = "결제 ID")
    private String tid;

    @Schema(description = "파트너 사용자 ID")
    private String partnerUserId;

    @Schema(description = "파트너 주문 ID")
    private String partnerOrderId;

    @Schema(description = "결제 방법 타입")
    private String paymentMethodType;

    @Schema(description = "상품 이름")
    private String itemName;

    @Schema(description = "수량")
    private int quantity;

    @Schema(description = "결제 금액 정보")
    private Amount amount;

    @Schema(description = "카드 정보")
    private CardInfo cardInfo;

    @Schema(description = "생성 일시")
    private String createdAt;

    @Schema(description = "승인 일시")
    private String approvedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Amount {
        private int total;
        private int taxFree;
        private int vat;
        private int discount;
        private int point;
        private int greenDeposit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardInfo {
        private String interestFreeInstall;
        private String bin;
        private String cardType;
        private String cardMid;
        private String approvedId;
        private String installMonth;
        private String installmentType;
        private String kakaopayPurchaseCorp;
        private String kakaopayPurchaseCorpCode;
        private String kakaopayIssuerCorp;
        private String kakaopayIssuerCorpCode;
    }
}
