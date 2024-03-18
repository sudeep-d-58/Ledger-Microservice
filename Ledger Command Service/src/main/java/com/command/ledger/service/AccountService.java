package com.command.ledger.service;

import com.command.ledger.exception.AccountStateChangeException;
import com.command.ledger.exception.AlreadyClosedException;
import com.command.ledger.exception.NotFoundException;
import com.command.ledger.exception.NotSupportedException;
import com.command.ledger.model.*;
import com.command.ledger.publisher.MessagePublisher;
import com.command.ledger.repository.AccountRepository;
import com.command.ledger.repository.WalletRepository;
import com.command.ledger.util.Utility;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MessagePublisher messagePublisher;


    @Transactional
    public Account createAccount(AccountCreateRequest accountCreateRequest) {
        //assuming new accounts created goes to OPEN State
        Account account = new Account(accountCreateRequest.getAccountName(), accountCreateRequest.getEntityId(), AccountState.OPEN);
        accountRepository.save(account);
        return account;
    }

    @Transactional
    public void closeAccount(long accountId) throws NotFoundException, AlreadyClosedException {
        Account account = getAccountById(accountId);
        AccountState state = account.getAccountState();
        if (state == AccountState.CLOSED) {
            log.info("Account with accountId : " + accountId + " is already closed.");
            throw new AlreadyClosedException("Account with accountId : " + accountId + " is already closed.");
        }
        account.setAccountState(AccountState.CLOSED);
        accountRepository.save(account);
    }

    @Transactional
    public void changeAccountState(long accountId, AccountState accountState) throws NotFoundException, AccountStateChangeException {
        Account account = getAccountById(accountId);
        AccountState state = account.getAccountState();
        if (state == accountState) {
            log.info("Account with accountId : " + accountId + " is already in " + accountState + " state");
            throw new AccountStateChangeException("Account with accountId : " + accountId + " is already in " + accountState + " state");
        }
        account.setAccountState(accountState);
        accountRepository.save(account);
    }

    private Account getAccountById(long accountId) throws NotFoundException {
        return accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account does not exist for accountId : " + accountId));
    }


    @Transactional(Transactional.TxType.REQUIRED)
    public Wallet addWallet(long accountId, WalletCreateRequest walletCreateRequest) throws NotFoundException, NotSupportedException {
        Account account = getAccountById(accountId);
        if (account.getAccountState() == AccountState.CLOSED) {
            throw new NotSupportedException("Account with accountId : " + accountId + " is closed.");
        }
        Wallet wallet = walletService.createWallet(walletCreateRequest);
        wallet.setAccount(account);
        accountRepository.save(account);
        HistoricalBalance historicalBalance = new HistoricalBalance();
        historicalBalance.setCurrBalance(wallet.getCurrBalance());
        historicalBalance.setPrevBalance(wallet.getPrevBalance());
        historicalBalance.setWalletId(wallet.getWalletId());
        historicalBalance.setAccountState(wallet.getAccount().getAccountState());
        historicalBalance.setTransactionId(null);
        historicalBalance.setAmount(null);
        historicalBalance.setEventType("Wallet Creation");
        historicalBalance.setDate(new Date());
        MessageEvent messageEvent = new MessageEvent(Utility.Wallet_Data_Publisher_1, historicalBalance);
        messagePublisher.publish(messageEvent);
        return wallet;
    }
}
