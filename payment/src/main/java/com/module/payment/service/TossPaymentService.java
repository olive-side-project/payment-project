package com.module.payment.service;

import com.module.payment.client.PaymentClient;
import com.module.user.enumCode.PaymentErrorCode;
import com.module.user.enumCode.PaymentStatus;
import com.module.payment.dto.PaymentsConfirmRequest;
import com.module.payment.dto.PaymentsConfirmResponse;
import com.module.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 토스 페이먼츠 결제 처리를 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class TossPaymentService {
    private final RedisService redisService;
    private final PaymentClient paymentClient;

    public PaymentsConfirmResponse confirmPayments(PaymentsConfirmRequest request) {
        String hashKey = "payment:" + request.getOrderId();

        // 이미 승인된 결제 여부 체크
        redisService.verifyPaymentCompletion(hashKey);

        // 결제 처리 잠금 설정
        redisService.acquireLock(hashKey);

        try {
            // 저장된 금액과 일치하는지 검증
            redisService.validateAmount(hashKey, request.getAmount());

            // 결제 승인 요청
            PaymentsConfirmResponse response = paymentClient.confirmPayments(request);

            // 승인 상태 업데이트
            redisService.updatePaymentStatus(hashKey, PaymentStatus.COMPLETED.getCode());
            return response;
        } catch (Exception e) {
            // 실패 상태 업데이트
            redisService.updatePaymentStatus(hashKey, PaymentStatus.FAILED.getCode());
            throw new PaymentException(String.valueOf(e), HttpStatus.BAD_REQUEST);
        } finally {
            // 잠금 해제
            redisService.releaseLock(hashKey);
        }
    }

    public void savePaymentInfo(String orderId, Long amount) {
        try {
            if (amount == null) {
                throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT.getMessage(), HttpStatus.BAD_REQUEST);
            }
            String hashKey = "payment:" + orderId;
            redisService.savePaymentInfo(hashKey, amount.toString());
        } catch (Exception e) {
            throw new PaymentException(PaymentErrorCode.SAVE_PAYMENT_INFO_FAILED.getCode(), e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
