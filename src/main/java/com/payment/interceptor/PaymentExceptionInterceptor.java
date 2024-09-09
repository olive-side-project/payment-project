package com.payment.interceptor;

import com.payment.exception.PaymentException;
import com.payment.enumCode.PaymentErrorCode;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 결제 요청 중 발생하는 예외를 처리합니다.
 */
public class PaymentExceptionInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (IOException e) {
            throw new PaymentException(PaymentErrorCode.IO_ERROR, e);
        } catch (Exception e) {
            throw new PaymentException(PaymentErrorCode.UNKNOWN_ERROR, e);
        }
    }
}
