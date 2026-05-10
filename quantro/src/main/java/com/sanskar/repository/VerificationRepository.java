package com.sanskar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanskar.model.VerificationCode;

public interface VerificationRepository extends JpaRepository<VerificationCode, Long> {
    public VerificationCode findByUserId(Long userId);
}

