package com.sanskar.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanskar.domain.WalletTransactionType;
import com.sanskar.model.Wallet;
import com.sanskar.model.WalletTransaction;
import com.sanskar.repository.TransactionRepository;
// import com.sanskar.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public WalletTransaction createTransaction(Wallet wallet,
            WalletTransactionType transactionType,
            String transferId,
            String purpose,
            Long amount) {
        
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(transactionType);
        transaction.setTransferId(transferId);
        transaction.setPurpose(purpose);
        transaction.setAmount(amount);
        
        return transactionRepository.save(transaction);
    }

    @Override
    public List<WalletTransaction> getTransactionsByWallet(Wallet wallet) {
        return transactionRepository.findByWallet(wallet);
    }
}
