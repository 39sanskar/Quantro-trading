package com.sanskar.controller;

import com.sanskar.service.EmailService;
import com.sanskar.service.UserService;
import com.sanskar.service.VerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    @SuppressWarnings("unused")
    private final VerificationService verificationService;

    @SuppressWarnings("unused")
    private final UserService userService;

    @SuppressWarnings("unused")
    private final EmailService emailService;

    @Autowired
    public VerificationController(
            VerificationService verificationService,
            UserService userService,
            EmailService emailService
    ) {
        this.verificationService = verificationService;
        this.userService = userService;
        this.emailService = emailService;
    }
}
