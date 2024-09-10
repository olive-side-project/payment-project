package com.payment.auth;

import com.payment.auth.dto.CryptoKeys;
import com.payment.auth.dto.EncryptedValue;
import com.payment.auth.dto.JWTKeys;
import com.payment.exception.PaymentException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * JWT 생성, 검증, 암호화 및 해독과 관련된 작업을 수행
 * JWT 서명과 암호화에 필요한 설정을 초기화하고 액세스 토큰과 리프레시 토큰을 생성
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    // JWT 서명을 생성하고 검증하는 데 사용되는 HMAC 서명 키들의 목록
    private final List<SecretKey> secretKeys = new ArrayList<>();


    // 암호화된 JWT 페이로드를 생성하고 해독하는 데 사용되는 대칭 키들의 목록
    private final List<SecretKey> cryptoSecretKeys = new ArrayList<>();


    // 암호화된 JWT 페이로드의 초기화 벡터(IV) 목록
    private final List<IvParameterSpec> cryptoIVs = new ArrayList<>();

    private final JWTKeys keys;
    private final CryptoKeys cryptoKeys;

    private final static String UserSeqKey = "userSeq";

    /*
        JWT 에서 사용되는 키와 암호화 설정을 초기화 (JWT 생성, 검증, 암호화 및 해독과 관련된 작업에서 사용)
     */
    @PostConstruct
    public void initKeys() {
        // JWT 키가 설정되지 않았거나 비어있으면 예외 처리
        if (keys == null || keys.getKeys() == null || keys.getKeys().isEmpty()) {
            throw new RuntimeException("jwt key not setting");
        }

        // JWT 서명 키 초기화: 설정에서 가져온 키 목록을 Base64 디코딩하고 HMAC SHA 서명 키로 변환
        for (String key : keys.getKeys()) {
            secretKeys.add(Keys.hmacShaKeyFor(Base64.getDecoder().decode(key)));
        }

        // 데이터 암호화 대칭 키 초기화: 설정에서 가져온 대칭 키 목록을 Base64 디코딩하고 AES 대칭 키로 변환
        for (String key : cryptoKeys.getKeys()) {
            cryptoSecretKeys.add(new SecretKeySpec(Base64.getDecoder().decode(key), "AES"));
        }

        // 데이터 암호화 초기화 벡터 (IV) 초기화: 설정에서 가져온 초기화 벡터 목록을 Base64 디코딩하고 IvParameterSpec 으로 변환
        for (String iv : cryptoKeys.getIvs()) {
            cryptoIVs.add(new IvParameterSpec(Base64.getDecoder().decode(iv)));
        }
    }

    private final Random random = new Random(System.nanoTime());

    private int randomIndex() {
        return random.nextInt(0, secretKeys.size());
    }

    public String generateJwt(long userSeq) {
        int keyIndex = randomIndex();

        JwtBuilder builder = Jwts.builder();
        String jwt = builder
                .claim(UserSeqKey, encrypt(String.valueOf(userSeq)))
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(secretKeys.get(keyIndex))
                .compact();
        String token = keyIndex + ":" + jwt;

        // 더 안전하게 하기 위해서 한 번 더 암호화
        return encrypt(token);
    }

    // 토큰 이중 암호화 용도
    public String encrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            KeyInfo key = getKey();
            IVInfo iv = getIv();

            // Cipher 객체를 암호화 모드로 초기화하고, 키와 초기화 벡터 설정
            cipher.init(Cipher.ENCRYPT_MODE, key.getSecretKey(), iv.getIvParameter());

            // 주어진 문자열을 UTF-8 인코딩으로 바이트 배열로 변환
            byte[] plainText = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));

            // 결과 문자열을 생성: 키와 초기화 벡터의 인덱스 정보와 암호화된 데이터를 Base64로 인코딩하여 조합
            return String.format("%d:%d:%s", key.getIndex(), iv.getIndex(), Base64.getUrlEncoder().encodeToString(plainText));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private KeyInfo getKey() {
        int index = random.nextInt(0, cryptoSecretKeys.size());
        return KeyInfo.builder().index(index).secretKey(cryptoSecretKeys.get(index)).build();
    }

    private IVInfo getIv() {
        int index = random.nextInt(0, cryptoIVs.size());
        return IVInfo.builder().index(index).ivParameter(cryptoIVs.get(index)).build();
    }

    @Data
    @Builder
    public static class KeyInfo {
        private int index;
        private SecretKey secretKey;
    }

    @Data
    @Builder
    public static class IVInfo {
        private int index;
        private IvParameterSpec ivParameter;
    }

    public long getUserSeqFromJWT(String jwt) {
        Jws<Claims> claimsJws = parseJWT(jwt);
        if (claimsJws == null)
            return 0L;

        return getUserSeq(claimsJws.getBody());
    }

    /**
     * @return : 토큰을 파싱하여 JWS(서명된 JWT) 객체를 생성하고 반환하는 parseJWT 메서드
     *         : 토큰을 해독(decrypt)하고 그 결과로부터 JWS 객체를 생성
     */
    public Jws<Claims> parseJWT(String token) {
        if (StringUtils.isEmpty(token))
            return null;

        // 토큰을 복호화하여 원래의 JWT 문자열 반환
        String jwt = decrypt(token);
        if (StringUtils.isEmpty(jwt)) {
            throw PaymentException.builder("FAIL_TOKEN_DECRYPT", HttpStatus.UNAUTHORIZED)
                    .message("TOKEN이 만료되었습니다")
                    .build();
        }

        int keyIndexPosition = jwt.indexOf(":");
        if (keyIndexPosition <= 0) {
            return null;
        }

        String keyIndex = jwt.substring(0, keyIndexPosition);
        String realJwt = jwt.substring(keyIndexPosition + 1);
        if (StringUtils.isEmpty(keyIndex) || !StringUtils.isNumeric(keyIndex))
            return null;

        int index = Integer.parseInt(keyIndex);

        // secretKeys 배열에서 index 위치에 있는 키를 사용하여 -> JWT 서명을 확인하고, 서명이 유효한 경우에만 클레임 정보를 추출
        return Jwts.parserBuilder().setSigningKey(secretKeys.get(index)).build().parseClaimsJws(realJwt);
    }

    /**
     * @param str : 암호화 된 문자열
     * @return : 복호화 된 평문 문자열 with SecretKey, ivSpec, Base64 decoding
     */
    public String decrypt(String str) {
        try {
            // 암호화된 문자열(세션 토큰)을 파싱하여 EncryptedValue 객체 생성
            EncryptedValue encryptedValue = parse(str);
            if (encryptedValue == null) {
                return null;
            }

            SecretKey secretKey = cryptoSecretKeys.get(encryptedValue.getKeyIndex());
            IvParameterSpec ivParameterSpec = cryptoIVs.get(encryptedValue.getIvIndex());

            // 복호화를 수행할 Cipher 객체를 생성
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            // Base64로 인코딩된 암호문을 복호화하고, 이를 바이트 배열로 얻음
            byte[] encrypted = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedValue.getEncryptedValue()));

            // 복호화된 바이트 배열을 UTF-8 문자열로 변환하여 암호화 되기 전의 평문을 반환
            return new String(encrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decrypt error", e);
            return null;
        }
    }

    /**
     * @param encrypted : 암호화된 데이터
     * @return : 암호화된 데이터를 파싱하여, keyIndex / ivIndex / encryptedValue 로 나눈 뒤 EncryptedValue 객체로 생성하고 반환
     */
    private EncryptedValue parse(String encrypted) {
        if (StringUtils.isEmpty(encrypted)) {
            return null;
        }

        try {
            //  ":" 기준으로 문자열을 3개의 토큰으로 나눔
            String[] tokens = encrypted.split("\\:", 3);
            int keyIndex = Integer.parseInt(tokens[0]);
            int ivIndex = Integer.parseInt(tokens[1]);
            return new EncryptedValue(keyIndex, ivIndex, tokens[2]);
        } catch (Exception e) {
            throw PaymentException.builder("FAIL_TOKEN_PARSE", HttpStatus.BAD_REQUEST)
                    .message("Failed to parse token indices.")
                    .build();
        }
    }

    private long getUserSeq(Claims body) {
        String encrypted = body.get(UserSeqKey, String.class);
        if (StringUtils.isEmpty(encrypted)) {
            return 0L;
        }

        String userSeqStr = decrypt(encrypted);
        if (StringUtils.isNotEmpty(userSeqStr) && StringUtils.isNumeric(userSeqStr)) {
            return Long.parseLong(userSeqStr);
        }

        return 0L;
    }
}
