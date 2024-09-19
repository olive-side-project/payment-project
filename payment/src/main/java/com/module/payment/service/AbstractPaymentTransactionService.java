package com.module.payment.service;

import com.module.common.exception.PaymentException;
import com.module.payment.client.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 결제 거래 내역 조회를 위한 공통 기능을 정의합니다.
 */
@RequiredArgsConstructor
public abstract class AbstractPaymentTransactionService<R, T> {
    protected final PaymentClient<R, T> paymentClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public List<?> findTransactionList(String startDateTime, String endDateTime) {
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
