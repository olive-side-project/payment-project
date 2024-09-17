package com.module.payment.service;

import com.module.payment.client.PaymentClient;
import com.module.payment.dto.PaymentsTransactionResponse;
import com.module.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 토스 페이먼츠 결제 조회를 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class TossPaymentsTransactionService {

    private final PaymentClient paymentClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public List<PaymentsTransactionResponse> findTransactionList(String startDateTime, String endDateTime) {
        validateDateTimeFormat(startDateTime, endDateTime);

        try {
            return paymentClient.getTransactions(startDateTime, endDateTime);
        } catch (Exception e) {
            throw new PaymentException("API_CALL_FAILED", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateDateTimeFormat(String startDate, String endDate) {
        try {
            DATE_TIME_FORMATTER.parse(startDate);
            DATE_TIME_FORMATTER.parse(endDate);
        } catch (DateTimeParseException e) {
            throw new PaymentException("INVALID_DATE_FORMAT", HttpStatus.BAD_REQUEST);
        }
    }
}
