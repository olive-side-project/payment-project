package com.module.payment.controller;

import com.module.payment.dto.PaymentSaveRequest;
import com.module.payment.dto.PaymentsConfirmRequest;
import com.module.payment.dto.PaymentsConfirmResponse;
import com.module.payment.service.TossPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "토스 페이먼츠 결제 API")
@RequiredArgsConstructor
public class TossPaymentsController {
    private final TossPaymentService tossPaymentService;

    @PostMapping("/toss/payments/confirm")
    @Operation(summary = "토스 결제 승인")
    public PaymentsConfirmResponse confirmPayments(@RequestBody @Valid PaymentsConfirmRequest confirmRequest) {
        return tossPaymentService.confirmPayments(confirmRequest);
    }

    @PostMapping("/toss/payments/info")
    @Operation(summary = "토스 결제주문번호 및 최종주문금액 저장")
    public void savePaymentInfo(@RequestBody @Valid PaymentSaveRequest paymentSaveRequest) {
        tossPaymentService.savePaymentInfo(paymentSaveRequest.getOrderId(), paymentSaveRequest.getAmount());
    }
}
