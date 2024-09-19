package com.module.payment.dto.toss;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentsConfirmRequest {
    @NotNull(message = "결제 키는 필수입니다.")
    @Schema(description = "결제 키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1")
    private String paymentKey;

    @NotNull(message = "주문ID는 필수입니다.")
    @Schema(description = "주문 ID", example = "a4CWyWY5m89PNh7xJwhk1")
    private String orderId;

    @Schema(description = "결제 금액", example = "1000")
    @NotNull(message = "금액은 필수입니다.")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amount;
}
