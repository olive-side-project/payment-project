package com.module.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentSaveRequest {
    @NotNull(message = "주문번호는 필수입니다.")
    @Schema(description = "주문번호", example = "a4CWyWY5m89PNh7xJwhk1")
    private String orderId;

    @Schema(description = "결제 금액", example = "1000")
    @NotNull(message = "금액은 필수입니다.")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amount;
}
