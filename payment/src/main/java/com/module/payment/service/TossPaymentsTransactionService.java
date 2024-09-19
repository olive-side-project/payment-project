package com.module.payment.service;

import com.module.payment.client.TossPaymentClient;
import com.module.payment.dto.toss.TossPaymentsTransactionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Toss 거래 조회를 담당합니다.
 */
@Service
public class TossPaymentsTransactionService extends AbstractPaymentTransactionService {

    public TossPaymentsTransactionService(TossPaymentClient tossPaymentClient) {
        super(tossPaymentClient);
    }

    @Override
    public List<TossPaymentsTransactionResponse> findTransactionList(String startDateTime, String endDateTime) {
        return (List<TossPaymentsTransactionResponse>) super.findTransactionList(startDateTime, endDateTime);
    }
}
