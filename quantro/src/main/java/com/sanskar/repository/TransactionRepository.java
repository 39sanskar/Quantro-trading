package com.sanskar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanskar.model.Wallet;
import com.sanskar.model.WalletTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {
    
  //   List<WalletTransaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
    
    List<WalletTransaction> findByWallet(Wallet wallet);
}

