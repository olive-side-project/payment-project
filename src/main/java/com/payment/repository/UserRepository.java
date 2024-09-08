package com.payment.repository;

import com.payment.enumCode.UserStatus;
import com.payment.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailAndStatus(String email, UserStatus status);
}
