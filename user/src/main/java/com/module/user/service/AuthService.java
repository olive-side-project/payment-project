package com.module.user.service;

import com.module.user.auth.AuthenticationUser;
import com.module.user.dao.UserDao;
import com.module.user.dto.LoginDto;
import com.module.user.dto.LoginDto.LoginRequest;
import com.module.user.dto.LoginDto.SignUpResponse;
import com.module.user.entity.UserEntity;
import com.module.user.enumCode.UserStatus;
import com.module.user.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;

    // 로그인과 세션 생성 및 JWT 발급을 처리하는 메서드
    @Transactional
    public AuthenticationUser authenticateAndCreateSession(LoginRequest loginRequest) {
        // 가입여부 확인
        UserEntity userEntity = findUserByEmail(loginRequest.getEmail());

        // 비밀번호 검증
        validatePassword(userEntity, loginRequest.getPassword());

        // 최종 로그인 시간 저장
        userDao.updateLastLoggedInAt(userEntity.getUserSeq());

        // 로그인 성공 시 사용자 정보를 AuthenticationUser로 반환
        return new AuthenticationUser(userEntity.getUserSeq(), userEntity.getEmail(), true);
    }

    private UserEntity findUserByEmail(String email) {
        UserEntity userEntity = userDao.findByEmailAndStatus(email, UserStatus.ACTIVE);
        if (userEntity == null) {
            throw new PaymentException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        return userEntity;
    }

    private void validatePassword(UserEntity userEntity, String password) {
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new PaymentException("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public SignUpResponse signup(LoginDto.SignUpRequest signUpRequest) {
        UserEntity existingUserEntity = userDao.findByEmailAndStatus(signUpRequest.getEmail(), UserStatus.ACTIVE);
        if (existingUserEntity != null) {
            throw new PaymentException("USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST);
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(signUpRequest.getEmail());
        newUserEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUserEntity.setStatus(UserStatus.ACTIVE);
        newUserEntity.setCreatedAt(LocalDateTime.now());
        userDao.save(newUserEntity);

        return new SignUpResponse(newUserEntity.getUserSeq(), newUserEntity.getEmail());
    }
}
