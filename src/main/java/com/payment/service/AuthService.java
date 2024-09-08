package com.payment.service;

import com.payment.dto.LoginDto.LoginRequest;
import com.payment.dto.LoginDto.LoginResponse;
import com.payment.dto.LoginDto.SignUpRequest;
import com.payment.dto.LoginDto.SignUpResponse;
import com.payment.entity.UserEntity;
import com.payment.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.payment.enumCode.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        UserEntity userEntity = authenticateUser(loginRequest);

        if (userEntity != null) {
            HttpSession newSession = createNewSession(request, userEntity.getEmail());
            return new LoginResponse(newSession.getId());
        } else {
            return new LoginResponse(null);
        }
    }

    // 사용자 인증
    private UserEntity authenticateUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        UserEntity userEntity = userRepository.findByEmailAndStatus(email, ACTIVE);
        if (userEntity != null && passwordEncoder.matches(password, userEntity.getPassword())) {
            return userEntity;
        }

        return null;
    }

    private HttpSession createNewSession(HttpServletRequest request, String email) {
        // 동일 사용자가 새로운 로그인 시도 시 기존 세션을 무효화하고 Redis에서 삭제
        invalidateOldSession(request);

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("user", email);

        // 세션 ID, 사용자 정보 레디스에 저장
        updateSessionInRedis(newSession.getId(), email);
        return newSession;
    }

    private void invalidateOldSession(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            String oldSessionId = oldSession.getId();
            oldSession.invalidate();
            redisTemplate.delete(oldSessionId);
        }
    }

    private void updateSessionInRedis(String sessionId, String email) {
        redisTemplate.opsForValue().set(sessionId, email, Duration.ofHours(1));
    }

    @Transactional
    public SignUpResponse signup(SignUpRequest signUpRequest) {
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();

        UserEntity existingUserEntity = userRepository.findByEmailAndStatus(email, ACTIVE);
        if (existingUserEntity != null) {
            return new SignUpResponse(null, "Already exist user");
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(email);
        newUserEntity.setPassword(passwordEncoder.encode(password));
        newUserEntity.setStatus(ACTIVE);
        newUserEntity.setCreatedAt(LocalDateTime.now());
        userRepository.save(newUserEntity);
        Long userId = newUserEntity.getUserSeq();

        return new SignUpResponse(userId, "User registered successfully");
    }

    public void logout(HttpSession session) {
        String sessionId = session.getId();
        session.invalidate();
        redisTemplate.delete(sessionId);
    }

    public void keepAlive(HttpSession session) {
        String sessionId = session.getId();
        redisTemplate.expire(sessionId, Duration.ofHours(1));
    }
}
