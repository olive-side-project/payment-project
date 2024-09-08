package com.payment.service;

import com.payment.client.PaymentClient;
import com.payment.dto.TossPaymentDto.PaymentRequest;
import com.payment.dto.TossPaymentDto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 토스 페이먼츠 결제 처리를 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final PaymentClient paymentClient;

    public PaymentResponse confirmPayments(PaymentRequest paymentRequest) {
        return paymentClient.confirmPayments(paymentRequest);
    }
}
