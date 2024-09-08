package com.payment.service;

import com.payment.dto.LoginDto.LoginRequest;
import com.payment.dto.LoginDto.LoginResponse;
import com.payment.dto.LoginDto.SignUpRequest;
import com.payment.dto.LoginDto.SignUpResponse;
import com.payment.entity.UserEntity;
import com.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity != null && passwordEncoder.matches(password, userEntity.getPassword())) {
            session.setAttribute("user", email);
            String sessionId = session.getId();
            redisTemplate.opsForValue().set(sessionId, email, Duration.ofHours(1));
            return new LoginResponse("Logged in successfully", sessionId);
        } else {
            return new LoginResponse("Invalid credentials", null);
        }
    }

    @Transactional
    public SignUpResponse signup(SignUpRequest signUpRequest) {
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();

        UserEntity existingUserEntity = userRepository.findByEmail(email);
        if (existingUserEntity != null) {
            return new SignUpResponse(null, "Already exist user");
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(email);
        newUserEntity.setPassword(passwordEncoder.encode(password));
        newUserEntity.setStatus("ACTIVE");
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
