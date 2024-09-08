package com.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class TossPaymentDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentRequest {
        @Schema(description = "결제 키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1")
        private String paymentKey;

        @Schema(description = "주문 ID", example = "a4CWyWY5m89PNh7xJwhk1")
        private String orderId;

        @Schema(description = "결제 금액", example = "1000")
        private Long amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentResponse {
        @Schema(description = "결제 서비스 제공자 ID")
        private String mId;

        @Schema(description = "최후 거래 키")
        private String lastTransactionKey;

        @Schema(description = "결제 키")
        private String paymentKey;

        @Schema(description = "주문 ID")
        private String orderId;

        @Schema(description = "주문 이름")
        private String orderName;

        @Schema(description = "세금 면제 금액")
        private double taxExemptionAmount;

        @Schema(description = "결제 상태")
        private String status;

        @Schema(description = "요청 일시")
        private String requestedAt;

        @Schema(description = "승인 일시")
        private String approvedAt;

        @Schema(description = "에스크로 사용 여부")
        private boolean useEscrow;

        @Schema(description = "문화비 사용 여부")
        private boolean cultureExpense;

        @Schema(description = "카드 정보")
        private CardDetails card;

        @Schema(description = "가상 계좌 정보")
        private String virtualAccount;

        @Schema(description = "이체 정보")
        private String transfer;

        @Schema(description = "휴대폰 정보")
        private String mobilePhone;

        @Schema(description = "상품권 정보")
        private String giftCertificate;

        @Schema(description = "현금 영수증 정보")
        private String cashReceipt;

        @Schema(description = "현금 영수증 목록")
        private String cashReceipts;

        @Schema(description = "할인 정보")
        private String discount;

        @Schema(description = "취소 정보")
        private String cancels;

        @Schema(description = "비밀 정보")
        private String secret;

        @Schema(description = "결제 타입")
        private String type;

        @Schema(description = "간편 결제 정보")
        private EasyPayDetails easyPay;

        @Schema(description = "간편 결제 금액")
        private double easyPayAmount;

        @Schema(description = "간편 결제 할인 금액")
        private double easyPayDiscountAmount;

        @Schema(description = "국가 코드")
        private String country;

        @Schema(description = "실패 정보")
        private String failure;

        @Schema(description = "부분 취소 가능 여부")
        private boolean isPartialCancelable;

        @Schema(description = "영수증 정보")
        private ReceiptDetails receipt;

        @Schema(description = "체크아웃 URL")
        private CheckoutDetails checkout;

        @Schema(description = "통화")
        private String currency;

        @Schema(description = "총 결제 금액")
        private double totalAmount;

        @Schema(description = "잔여 금액")
        private double balanceAmount;

        @Schema(description = "제공 금액")
        private double suppliedAmount;

        @Schema(description = "부가세")
        private double vat;

        @Schema(description = "면세 금액")
        private double taxFreeAmount;

        @Schema(description = "결제 방법")
        private String method;

        @Schema(description = "버전")
        private String version;


        public static class CardDetails {
            @Schema(description = "발급사 코드")
            private String issuerCode;

            @Schema(description = "인수사 코드")
            private String acquirerCode;

            @Schema(description = "카드 번호")
            private String number;

            @Schema(description = "할부 개월 수")
            private int installmentPlanMonths;

            @Schema(description = "무이자 여부")
            private boolean isInterestFree;

            @Schema(description = "이자 부담자")
            private String interestPayer;

            @Schema(description = "승인 번호")
            private String approveNo;

            @Schema(description = "카드 포인트 사용 여부")
            private boolean useCardPoint;

            @Schema(description = "카드 유형")
            private String cardType;

            @Schema(description = "카드 소유자 유형")
            private String ownerType;

            @Schema(description = "카드 인수 상태")
            private String acquireStatus;

            @Schema(description = "영수증 URL")
            private String receiptUrl;

            @Schema(description = "결제 금액")
            private double amount;
        }

        public static class EasyPayDetails {
            @Schema(description = "제공자")
            private String provider;

            @Schema(description = "금액")
            private double amount;

            @Schema(description = "할인 금액")
            private double discountAmount;
        }

        public static class ReceiptDetails {
            @Schema(description = "영수증 URL")
            private String url;
        }

        public static class CheckoutDetails {
            @Schema(description = "체크아웃 URL")
            private String url;
        }
    }

}
