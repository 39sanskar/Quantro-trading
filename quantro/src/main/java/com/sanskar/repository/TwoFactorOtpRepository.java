package com.sanskar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanskar.model.TwoFactorOTP;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {
    TwoFactorOTP findByUserId(Long userId);
}

