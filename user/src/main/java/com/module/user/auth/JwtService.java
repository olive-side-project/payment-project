package com.module.user.auth;

import com.module.common.exception.PaymentException;
import com.module.user.auth.dto.CryptoKeys;
import com.module.user.auth.dto.EncryptedValue;
import com.module.user.auth.dto.JWTKeys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

/**
 * JWT 생성, 검증, 암호화 및 해독과 관련된 작업을 수행
 * JWT 서명과 암호화에 필요한 설정을 초기화하고 액세스 토큰과 리프레시 토큰을 생성
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private final List<SecretKey> secretKeys = new ArrayList<>();
    private final List<SecretKey> cryptoSecretKeys = new ArrayList<>();
    private final List<IvParameterSpec> cryptoIVs = new ArrayList<>();

    private final JWTKeys keys;
    private final CryptoKeys cryptoKeys;

    private final static String USER_SEQ_KEY = "userSeq";

    @PostConstruct
    public void initKeys() {
        if (keys == null || keys.getKeys() == null || keys.getKeys().isEmpty()) {
            throw new RuntimeException("jwt key not setting");
        }

        for (String key : keys.getKeys()) {
            secretKeys.add(Keys.hmacShaKeyFor(Base64.getDecoder().decode(key)));
        }

        for (String key : cryptoKeys.getKeys()) {
            cryptoSecretKeys.add(new SecretKeySpec(Base64.getDecoder().decode(key), "AES"));
        }

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
                .claim(USER_SEQ_KEY, encrypt(String.valueOf(userSeq))) // 들여쓰기 추가
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(secretKeys.get(keyIndex))
                .compact();
        String token = keyIndex + "-" + jwt;

        return encrypt(token);
    }

    public String encrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            KeyInfo key = getKey();
            IVInfo iv = getIv();

            cipher.init(Cipher.ENCRYPT_MODE, key.getSecretKey(), iv.getIvParameter());
            byte[] plainText = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));

            return String.format("%d-%d-%s", key.getIndex(), iv.getIndex(), Base64.getUrlEncoder().encodeToString(plainText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException |
                 InvalidAlgorithmParameterException e) {
            log.error("Encryption error", e);
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
        if (claimsJws == null) {
            return 0L;
        }

        return getUserSeq(claimsJws.getBody());
    }

    public Jws<Claims> parseJWT(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        String jwt = decrypt(token);
        if (StringUtils.isEmpty(jwt)) {
            throw PaymentException.builder("FAIL_TOKEN_DECRYPT", HttpStatus.UNAUTHORIZED)
                .message("TOKEN이 만료되었습니다")
                .build();
        }

        int keyIndexPosition = jwt.indexOf("-");
        if (keyIndexPosition <= 0) {
            return null;
        }

        String keyIndex = jwt.substring(0, keyIndexPosition);
        String realJwt = jwt.substring(keyIndexPosition + 1);
        if (StringUtils.isEmpty(keyIndex) || !StringUtils.isNumeric(keyIndex)) {
            return null;
        }

        int index = Integer.parseInt(keyIndex);
        return Jwts.parserBuilder().setSigningKey(secretKeys.get(index)).build().parseClaimsJws(realJwt);
    }

    public String decrypt(String str) {
        try {
            EncryptedValue encryptedValue = parse(str);
            if (encryptedValue == null) {
                return null;
            }

            SecretKey secretKey = cryptoSecretKeys.get(encryptedValue.getKeyIndex());
            IvParameterSpec ivParameterSpec = cryptoIVs.get(encryptedValue.getIvIndex());

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedValue.getEncryptedValue()));
            return new String(encrypted, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException |
                 InvalidAlgorithmParameterException e) {
            log.error("Decrypt error", e);
            return null;
        }
    }

    private EncryptedValue parse(String encrypted) {
        if (StringUtils.isEmpty(encrypted)) {
            return null;
        }

        try {
            String[] tokens = encrypted.split("-", 3);
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
        String encrypted = body.get(USER_SEQ_KEY, String.class);
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
