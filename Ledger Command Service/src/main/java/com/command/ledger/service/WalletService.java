package com.command.ledger.service;

import com.command.ledger.exception.InsufficientFundsException;
import com.command.ledger.exception.NotFoundException;
import com.command.ledger.exception.NotSupportedException;
import com.command.ledger.model.*;
import com.command.ledger.publisher.MessagePublisher;
import com.command.ledger.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MessagePublisher messagePublisher;


    @Transactional
    public Wallet createWallet(WalletCreateRequest walletCreateRequest) {
        Wallet wallet = new Wallet();
        wallet.setAssetType(walletCreateRequest.getAssetType());
        wallet.setCurrBalance(walletCreateRequest.getBalance());
        wallet.setPrevBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);
        return wallet;
    }

    public Wallet getWalletById(long walletId) throws NotFoundException {
        return walletRepository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet does not exist for walletId : " + walletId));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public HistoricalBalance debit(Long walletId, BigDecimal amount) throws InsufficientFundsException, NotFoundException {
        Wallet wallet = getWalletById(walletId);
        HistoricalBalance historicalBalance = debitWallet(wallet, amount);
        walletRepository.save(wallet);
        return historicalBalance;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public HistoricalBalance credit(Long walletId, BigDecimal amount) throws NotFoundException {
        Wallet wallet = getWalletById(walletId);
        wallet.setPrevBalance(wallet.getCurrBalance());
        wallet.setCurrBalance(wallet.getCurrBalance().add(amount));
        walletRepository.save(wallet);
        HistoricalBalance historicalBalance = new HistoricalBalance();
        historicalBalance.setCurrBalance(wallet.getCurrBalance());
        historicalBalance.setPrevBalance(wallet.getPrevBalance());
        historicalBalance.setWalletId(walletId);
        historicalBalance.setAccountState(wallet.getAccount().getAccountState());
        historicalBalance.setTransactionId(null);
        historicalBalance.setAmount(amount);
        historicalBalance.setEventType("Credit");
        historicalBalance.setDate(new Date());
        return historicalBalance;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private HistoricalBalance debitWallet(Wallet wallet, BigDecimal amount) throws InsufficientFundsException {
        if (wallet.getCurrBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in wallet");
        }
        wallet.setPrevBalance(wallet.getCurrBalance());
        wallet.setCurrBalance(wallet.getCurrBalance().subtract(amount));
        walletRepository.save(wallet);
        HistoricalBalance historicalBalance = new HistoricalBalance();
        historicalBalance.setCurrBalance(wallet.getCurrBalance());
        historicalBalance.setPrevBalance(wallet.getPrevBalance());
        historicalBalance.setWalletId(wallet.getWalletId());
        historicalBalance.setAccountState(wallet.getAccount().getAccountState());
        historicalBalance.setTransactionId(null);
        historicalBalance.setAmount(amount);
        historicalBalance.setEventType("Debit");
        historicalBalance.setDate(new Date());
        return historicalBalance;
    }


    public Wallet updateWalletBalance(long walletId, UpdateBalance newBalance) throws NotFoundException, NotSupportedException {
        Wallet wallet = getWalletById(walletId);
        if (wallet.getAccount().getAccountState() == AccountState.CLOSED) {
            log.warn("Cannot update a wallet associated with a closed account: {}", wallet.getAccount().getAccountId());
            throw new NotSupportedException("Cannot update a wallet associated with a closed account: " + wallet.getAccount().getAccountId());
        }
        wallet.setPrevBalance(wallet.getCurrBalance());
        wallet.setCurrBalance(newBalance.getNewBalance());
        MessageEvent messageEvent = new MessageEvent();
        return walletRepository.save(wallet);
    }
}
