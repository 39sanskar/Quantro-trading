package com.sanskar.service;

import com.sanskar.domain.WalletTransactionType;
import com.sanskar.model.Wallet;
import com.sanskar.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(Wallet wallet,
        WalletTransactionType type,
        String transferId,
        String purpose,
        Long amount
    );

    List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type);

}
