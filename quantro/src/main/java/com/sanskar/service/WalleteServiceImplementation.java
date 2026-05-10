package com.sanskar.service;

import com.sanskar.domain.OrderType;
import com.sanskar.exception.WalletException;
import com.sanskar.model.*;
import com.sanskar.repository.WalletRepository;
import com.sanskar.repository.WalletTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class WalleteServiceImplementation implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    // Create Wallet
    public Wallet genrateWallete(User user) {
        Wallet wallet = new Wallet();

        wallet.setUser(user);

        // Optional: initialize with zero balance
        if (wallet.getBalance() == null) {
            wallet.setBalance(BigDecimal.ZERO);
        }

        return walletRepository.save(wallet);
    }

    // Get User Wallet
    @Override
    public Wallet getUserWallet(User user) throws WalletException {

        Wallet wallet = walletRepository.findByUserId(user.getId());

        if (wallet != null) {
            return wallet;
        }

        wallet = genrateWallete(user);

        return wallet;
    }

    // Find Wallet By Id
    @Override
    public Wallet findWalletById(Long id) throws WalletException {

        Optional<Wallet> wallet = walletRepository.findById(id);

        if (wallet.isPresent()) {
            return wallet.get();
        }

        throw new WalletException("Wallet not found with id " + id);
    }

    // Wallet Transfer
    @Override
    public Wallet walletToWalletTransfer(
            User sender,
            Wallet receiverWallet,
            Long amount
    ) throws WalletException {

        Wallet senderWallet = getUserWallet(sender);

        if (senderWallet.getBalance()
                .compareTo(BigDecimal.valueOf(amount)) < 0) {

            throw new WalletException("Insufficient balance...");
        }

        // Deduct sender balance
        BigDecimal senderBalance = senderWallet.getBalance()
                .subtract(BigDecimal.valueOf(amount));

        senderWallet.setBalance(senderBalance);

        walletRepository.save(senderWallet);

        // Add receiver balance
        BigDecimal receiverBalance = receiverWallet.getBalance()
                .add(BigDecimal.valueOf(amount));

        receiverWallet.setBalance(receiverBalance);

        walletRepository.save(receiverWallet);

        return senderWallet;
    }

    // Pay Order
    @Override
    public Wallet payOrderPayment(Order order, User user)
            throws WalletException {

        Wallet wallet = getUserWallet(user);

        WalletTransaction walletTransaction = new WalletTransaction();

        walletTransaction.setWallet(wallet);

        walletTransaction.setPurpose(
                order.getOrderType() + " "
                        + order.getOrderItem().getCoin().getId()
        );

        walletTransaction.setDate(LocalDate.now());

        walletTransaction.setTransferId(
                order.getOrderItem().getCoin().getSymbol()
        );

        // BUY Order
        if (order.getOrderType().equals(OrderType.BUY)) {

            walletTransaction.setAmount(
                    -order.getPrice().longValue()
            );

            BigDecimal newBalance = wallet.getBalance()
                    .subtract(order.getPrice());

            // FIXED LOGIC
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {

                throw new WalletException(
                        "Insufficient funds for this transaction."
                );
            }

            wallet.setBalance(newBalance);
        }

        // SELL Order
        else if (order.getOrderType().equals(OrderType.SELL)) {

            walletTransaction.setAmount(
                    order.getPrice().longValue()
            );

            BigDecimal newBalance = wallet.getBalance()
                    .add(order.getPrice());

            wallet.setBalance(newBalance);
        }

        walletTransactionRepository.save(walletTransaction);

        walletRepository.save(wallet);

        return wallet;
    }

    // Add Balance To Wallet
    @Override
    public Wallet addBalanceToWallet(Wallet wallet, Long money)
            throws WalletException {

        if (money <= 0) {
            throw new WalletException(
                    "Amount must be greater than zero"
            );
        }

        BigDecimal currentBalance = wallet.getBalance();

        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        BigDecimal newBalance = currentBalance.add(
                BigDecimal.valueOf(money)
        );

        // FIXED: Balance added ONLY ONCE
        wallet.setBalance(newBalance);

        walletRepository.save(wallet);

        System.out.println(
                "Updated wallet balance: " + wallet.getBalance()
        );

        return wallet;
    }
}
