package com.payment.repository;

import com.payment.enumCode.UserStatus;
import com.payment.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailAndStatus(String email, UserStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLoggedAt = :lastLoggedAt WHERE u.userSeq = :userSeq")
    void updateLastLoggedInAt(@Param("userSeq") long userSeq,
                              @Param("lastLoggedAt") LocalDateTime lastLoggedAt);
}
