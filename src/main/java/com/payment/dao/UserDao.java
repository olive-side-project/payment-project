package com.payment.dao;

import com.payment.entity.UserEntity;
import com.payment.enumCode.UserStatus;
import com.payment.repository.UserRepository;
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

    private final JPAQueryFactory queryFactory;

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
