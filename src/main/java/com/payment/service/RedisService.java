package com.payment.service;

import com.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.payment.enumCode.PaymentErrorCode.*;
import static com.payment.enumCode.PaymentStatus.*;
import static java.lang.Boolean.*;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final HashOperations<String, String, String> hashOps;

    private static final String STATUS_FIELD = "status";
    private static final String AMOUNT_FIELD = "amount";

    @Value("${payment.expiry.minutes:10}")
    private long expiryMinutes;

    public void verifyPaymentCompletion(String hashKey) {
        String status = hashOps.get(hashKey, STATUS_FIELD);
        if (status == null) {
            return;
        }
        if (COMPLETED.getCode().equals(status)) {
            throw new PaymentException(PAYMENT_ALREADY_COMPLETED.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public void acquireLock(String hashKey) {
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(hashKey + ":lock", LOCKED.getCode(), expiryMinutes, TimeUnit.MINUTES);
        if (FALSE.equals(lockAcquired)) {
            throw new PaymentException(PAYMENT_PROCESSING_IN_PROGRESS.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void validateAmount(String hashKey, Long requestAmount) {
        String storedAmount = hashOps.get(hashKey, AMOUNT_FIELD);

        // 저장된 금액이 존재하지 않는 경우
        if (storedAmount == null) {
            throw new PaymentException(PAYMENT_PROCESSING_FAILED.getMessage(), HttpStatus.NOT_FOUND);
        }

        // 저장된 금액과 요청된 금액이 일치하지 않는 경우
        if (!storedAmount.equals(requestAmount.toString())) {
            throw new PaymentException(AMOUNT_MISMATCH.getCode(), AMOUNT_MISMATCH.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void updatePaymentStatus(String hashKey, String status) {
        hashOps.put(hashKey, STATUS_FIELD, status);
        redisTemplate.expire(hashKey, expiryMinutes, TimeUnit.MINUTES);
    }

    public void releaseLock(String hashKey) {
        redisTemplate.delete(hashKey + ":lock");
    }

    public void savePaymentInfo(String hashKey, String amount) {
        hashOps.put(hashKey, AMOUNT_FIELD, amount);
        redisTemplate.expire(hashKey, expiryMinutes, TimeUnit.MINUTES);
    }
}
