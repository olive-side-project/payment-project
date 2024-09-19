package com.module.payment.client;

import com.module.payment.dto.kakao.KakaoPaymentsConfirmRequest;
import com.module.payment.dto.kakao.KakaoPaymentsConfirmResponse;
import com.module.payment.dto.kakao.KakaoPaymentsTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.List;

/**
 * Kakao API와의 통신을 담당합니다
 * (( 현재는 결제 모듈 확장성을 고려하여 임시 코드로 작성되었습니다. ))
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPaymentClient implements PaymentClient<KakaoPaymentsConfirmResponse, KakaoPaymentsConfirmRequest> {
    @Override
    public KakaoPaymentsConfirmResponse confirmPayments(KakaoPaymentsConfirmRequest confirmRequest) {
        return null;
    }

    @Override
    public List<KakaoPaymentsTransactionResponse> getTransactions(String startDate, String endDate) {
        return Collections.emptyList();
    }
}
