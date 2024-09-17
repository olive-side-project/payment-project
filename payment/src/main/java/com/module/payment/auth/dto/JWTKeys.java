package com.module.payment.auth.dto;

import lombok.Data;

import java.util.List;

/*
    JWT 토큰 생성 및 검증을 위한 키 설정을 담는 필드
 */
@Data
public class JWTKeys {
    private List<String> keys;
}
