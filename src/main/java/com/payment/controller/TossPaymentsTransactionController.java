package com.payment.controller;

import com.payment.annotation.Public;
import com.payment.dto.PaymentsTransactionResponse;
import com.payment.service.TossPaymentsTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "토스 페이먼츠 조회 API")
@RequiredArgsConstructor
public class TossPaymentsTransactionController {
    private final TossPaymentsTransactionService transactionService;

    @Public
    @GetMapping("/toss/payments/transactions")
    @Operation(summary = "토스 거래 조회",
               description = "날짜형식 포맷 : yyyy-mm-ddT23:59:59  (ex) 2024-09-01T23:59:59")
    public List<PaymentsTransactionResponse> findTransactions(
            @RequestParam(name = "startDateTime") String startDateTime,
            @RequestParam(name = "endDateTime") String endDateTime) {
        return transactionService.findTransactionList(startDateTime, endDateTime);
    }
}
