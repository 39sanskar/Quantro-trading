package com.sanskar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.sanskar.model.User;
import com.sanskar.model.Wallet;
import com.sanskar.model.WalletTransaction;
import com.sanskar.service.TransactionService;
import com.sanskar.service.UserService;
import com.sanskar.service.WalletService;

@RestController
public class TransactionController {


  @Autowired
  private WalletService walletService;

  @Autowired
  private UserService userService;

  @Autowired
  private TransactionService transactionService;

  @GetMapping("api/transactions")
  public ResponseEntity<List<WalletTransaction>> getUserWallet(
    @RequestHeader("Authorization") String jwt) throws Exception {
      User user = userService.findUserProfileByJwt(jwt);

      Wallet wallet = walletService.getUserWallet(user);

      List<WalletTransaction> transactionList=transactionService.getTransactionsByWallet(wallet);

      return new ResponseEntity<>(transactionList, HttpStatus.ACCEPTED);
    }
}
