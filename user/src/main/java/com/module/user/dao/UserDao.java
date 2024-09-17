package com.module.user.dao;

import com.module.user.entity.UserEntity;
import com.module.common.enumCode.UserStatus;
import com.module.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserDao {
    private final UserRepository userRepository;

    public UserEntity findByEmailAndStatus(String email, UserStatus status) {
        return userRepository.findByEmailAndStatus(email, status);
    }

    @Transactional
    public void updateLastLoggedInAt(long userSeq) {
        userRepository.updateLastLoggedInAt(userSeq, LocalDateTime.now());
    }

    @Transactional
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
}
