package com.sanskar.service;

import java.util.List;

import com.sanskar.domain.WalletTransactionType;
import com.sanskar.model.Wallet;
import com.sanskar.model.WalletTransaction;

public interface TransactionService {

  WalletTransaction createTransaction(Wallet wallet,
     WalletTransactionType transactionType,
     String transferId,
     String purpose,
     Long amount

  );

  List<WalletTransaction> getTransactionsByWallet(Wallet wallet);

}
