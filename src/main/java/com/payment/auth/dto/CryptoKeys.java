package com.payment.auth.dto;

import lombok.Data;

import java.util.List;

/*
    데이터 암호화를 위한 대칭 키 및 초기화 벡터 설정을 담는 필드
 */
@Data
public class CryptoKeys {
    private List<String> keys;
    private List<String> ivs;
}
