package com.module.payment.service;

import com.module.common.service.RedisService;
import com.module.payment.client.KakaoPaymentClient;
import com.module.payment.dto.kakao.KakaoPaymentsConfirmRequest;
import com.module.payment.dto.kakao.KakaoPaymentsConfirmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Kakao 결제 승인 요청을 담당합니다.
 */
@Slf4j
@Component("kakaoPaymentService")
public class KakaoPaymentsService extends AbstractPaymentService {

    public KakaoPaymentsService(RedisService redisService, KakaoPaymentClient kakaoPaymentClient) {
        super(redisService, kakaoPaymentClient);
    }

    @Override
    protected String getRedisKeyPrefix() {
        return "kakaoPayment:";
    }

    @Override
    protected String getOrderId(Object request) {
        if (!(request instanceof KakaoPaymentsConfirmRequest)) {
            throw new IllegalArgumentException("Invalid request type for KakaoPaymentsService");
        }
        KakaoPaymentsConfirmRequest kakaoRequest = (KakaoPaymentsConfirmRequest) request;
        return kakaoRequest.getTid();
    }


    @Override
    protected Long getAmount(Object request) {
        if (!(request instanceof KakaoPaymentsConfirmRequest kakaoRequest)) {
            throw new IllegalArgumentException("Invalid request type for KakaoPaymentsService");
        }

        return kakaoRequest.getTotalAmount();
    }

    // KakaoPaymentsConfirmRequest에 맞는 타입 안전성을 유지하기 위해 메서드 오버로딩
    public KakaoPaymentsConfirmResponse confirmPayments(KakaoPaymentsConfirmRequest request) {
        Object response = super.confirmPayments(request); // Object 타입의 부모 메서드 호출
        if (!(response instanceof KakaoPaymentsConfirmResponse)) {
            throw new IllegalArgumentException("Invalid response type for KakaoPaymentsService");
        }
        return (KakaoPaymentsConfirmResponse) response;
    }
}
