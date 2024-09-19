package com.module.payment.service;

import com.module.common.enumCode.PaymentStatus;
import com.module.common.exception.PaymentException;
import com.module.common.service.RedisService;
import com.module.payment.client.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


/**
 * 결제 승인 요청을 위한 공통 기능을 정의합니다.
 */
@Service
@RequiredArgsConstructor
public abstract class AbstractPaymentService<R, T> {
    protected final RedisService redisService;
    protected final PaymentClient<R, T> paymentClient;

    protected abstract String getRedisKeyPrefix();
    protected abstract String getOrderId(T request);
    protected abstract Long getAmount(T request);

    public R confirmPayments(T request) {
        String hashKey = getRedisKeyPrefix() + getOrderId(request);
        // 결제 완료 여부 체크
        redisService.verifyPaymentCompletion(hashKey);
        // 결제 처리 잠금 설정
        redisService.acquireLock(hashKey);

        try {
            // 저장된 금액과 일치하는지 검증
            redisService.validateAmount(hashKey, getAmount(request));

            // 각 결제 클라이언트를 통해 결제 승인 요청 처리
            R response = paymentClient.confirmPayments(request);

            // 승인 상태 업데이트
            redisService.updatePaymentStatus(hashKey, PaymentStatus.COMPLETED.getCode());

            return response;
        } catch (Exception e) {
            handlePaymentException(hashKey, e);
            return null;
        } finally {
            // 결제 처리 잠금 해제
            redisService.releaseLock(hashKey);
        }
    }

    private void handlePaymentException(String hashKey, Exception e) {
        redisService.updatePaymentStatus(hashKey, PaymentStatus.FAILED.getCode());

        if (e instanceof PaymentException) {
            throw (RuntimeException) e;
        }

        throw new PaymentException("Payment processing failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    public void savePaymentInfo(String orderId, Long amount) {
        try {
            String hashKey = getRedisKeyPrefix() + orderId;
            redisService.savePaymentInfo(hashKey, amount.toString());
        } catch (Exception e) {
            throw new PaymentException("SAVE_PAYMENT_INFO_FAILED", "Failed to save payment info: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
