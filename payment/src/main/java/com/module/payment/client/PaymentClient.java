package com.module.payment.client;

import java.util.List;

public interface PaymentClient<R, T> {
    R confirmPayments(T request);
    List<?> getTransactions(String startDate, String endDate);
}
