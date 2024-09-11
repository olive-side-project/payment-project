package com.payment.controller;

import com.payment.annotation.Public;
import com.payment.dto.PaymentSaveRequest;
import com.payment.dto.PaymentsConfirmRequest;
import com.payment.dto.PaymentsConfirmResponse;
import com.payment.service.TossPaymentService;
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

    @Public
    @PostMapping("/toss/payments/confirm")
    @Operation(summary = "토스 결제 승인")
    public PaymentsConfirmResponse confirmPayments(@RequestBody @Valid PaymentsConfirmRequest confirmRequest) {
        return tossPaymentService.confirmPayments(confirmRequest);
    }

    @Public
    @PostMapping("/toss/payment/info")
    @Operation(summary = "토스 결제주문번호 및 최종주문금액 저장")
    public void savePaymentInfo(@RequestBody @Valid PaymentSaveRequest paymentSaveRequest) {
        tossPaymentService.savePaymentInfo(paymentSaveRequest.getOrderId(), paymentSaveRequest.getAmount());
    }
}
