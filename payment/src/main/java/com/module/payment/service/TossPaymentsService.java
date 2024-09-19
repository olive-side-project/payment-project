package com.module.payment.service;

import com.module.common.service.RedisService;
import com.module.payment.client.TossPaymentClient;
import com.module.payment.dto.toss.TossPaymentsConfirmRequest;
import com.module.payment.dto.toss.TossPaymentsConfirmResponse;
import com.module.common.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Toss 결제 승인 요청을 담당합니다.
 */
@Slf4j
@Component("tossPaymentService")
public class TossPaymentsService extends AbstractPaymentService {

    public TossPaymentsService(RedisService redisService, TossPaymentClient tossPaymentClient) {
        super(redisService, tossPaymentClient);
    }

    @Override
    protected String getRedisKeyPrefix() {
        return "tossPayment:";
    }

    @Override
    protected String getOrderId(Object request) {
        return getRequestProperty(request, TossPaymentsConfirmRequest::getOrderId, "OrderId extraction failed");
    }

    @Override
    protected Long getAmount(Object request) {
        return getRequestProperty(request, TossPaymentsConfirmRequest::getAmount, "Amount extraction failed");
    }

    // 공통 타입 검증 및 변환
    private <T> T getRequestProperty(Object request, Function<TossPaymentsConfirmRequest, T> extractor, String errorMessage) {
        try {
            TossPaymentsConfirmRequest tossRequest = validateAndCastRequest(request);
            return extractor.apply(tossRequest);
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentException(errorMessage + ": " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 타입 검증 및 캐스팅
    private TossPaymentsConfirmRequest validateAndCastRequest(Object request) {
        if (!(request instanceof TossPaymentsConfirmRequest)) {
            throw new PaymentException("Invalid request type: TossPaymentsConfirmRequest required", HttpStatus.BAD_REQUEST);
        }
        return (TossPaymentsConfirmRequest) request;
    }

    // 타입 안정성 유지를 위한 오버로딩
    public TossPaymentsConfirmResponse confirmPayments(TossPaymentsConfirmRequest tossPaymentsConfirmRequest) {
            Object response = super.confirmPayments(tossPaymentsConfirmRequest);

            if (!(response instanceof TossPaymentsConfirmResponse)) {
                throw new PaymentException("Invalid response type: TossPaymentsConfirmResponse required", HttpStatus.BAD_REQUEST);
            }

            return (TossPaymentsConfirmResponse) response;
    }
}
