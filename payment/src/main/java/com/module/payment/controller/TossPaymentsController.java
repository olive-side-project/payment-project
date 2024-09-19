package com.module.payment.controller;

import com.module.payment.dto.toss.TossPaymentSaveRequest;
import com.module.payment.dto.toss.TossPaymentsConfirmRequest;
import com.module.payment.dto.toss.TossPaymentsConfirmResponse;
import com.module.payment.service.TossPaymentsService;
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
    private final TossPaymentsService tossPaymentService;

    @PostMapping("/toss/payments/confirm")
    @Operation(summary = "토스 결제 승인")
    public TossPaymentsConfirmResponse tossConfirmPayments(@RequestBody @Valid TossPaymentsConfirmRequest confirmRequest) {
        return tossPaymentService.confirmPayments(confirmRequest);
    }

    @PostMapping("/toss/payments/info")
    @Operation(summary = "토스 결제주문번호 및 최종주문금액 저장")
    public void savePaymentInfo(@RequestBody @Valid TossPaymentSaveRequest tossPaymentSaveRequest) {
        tossPaymentService.savePaymentInfo(tossPaymentSaveRequest.getOrderId(), tossPaymentSaveRequest.getAmount());
    }
}
