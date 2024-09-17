package com.module.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsTransactionResponse {
    @Schema(description = "결제 서비스 제공자 ID")
    private String mId;

    @Schema(description = "거래 키")
    private String transactionKey;

    @Schema(description = "결제 키")
    private String paymentKey;

    @Schema(description = "주문 ID")
    private String orderId;

    @Schema(description = "결제 방법")
    private String method;

    @Schema(description = "고객 키")
    private String customerKey;

    @Schema(description = "에스크로 사용 여부")
    private Boolean useEscrow;

    @Schema(description = "영수증 URL")
    private String receiptUrl;

    @Schema(description = "거래 상태")
    private String status;

    @Schema(description = "거래 일시")
    private LocalDateTime transactionAt;

    @Schema(description = "통화")
    private String currency;

    @Schema(description = "금액")
    private Integer amount;
}
