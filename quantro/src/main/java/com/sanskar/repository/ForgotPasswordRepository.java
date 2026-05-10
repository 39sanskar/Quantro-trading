package com.sanskar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanskar.model.ForgotPasswordToken;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {
    ForgotPasswordToken findByUserId(Long userId);

}
