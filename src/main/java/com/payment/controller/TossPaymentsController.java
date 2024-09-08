package com.payment.controller;

import com.payment.dto.TossPaymentDto.PaymentRequest;
import com.payment.dto.TossPaymentDto.PaymentResponse;
import com.payment.service.TossPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public PaymentResponse confirmPayments(@RequestBody PaymentRequest paymentRequest) {
        return tossPaymentService.confirmPayments(paymentRequest);
    }
}
