package com.sanskar.model;


import com.sanskar.domain.VerificationType;

import lombok.Data;


@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;
    private VerificationType sendTo;
}
