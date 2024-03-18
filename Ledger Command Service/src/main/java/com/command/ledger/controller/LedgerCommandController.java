package com.command.ledger.controller;

import com.command.ledger.exception.*;
import com.command.ledger.model.*;
import com.command.ledger.service.AccountService;
import com.command.ledger.service.TransactionService;
import com.command.ledger.service.WalletService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/command")
public class LedgerCommandController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping(value = "/create/account", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Account> createAccount(@RequestBody AccountCreateRequest accountCreateRequest){
        Account account = accountService.createAccount(accountCreateRequest);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @PostMapping("/add/wallet/{accountId}")
    public ResponseEntity<Wallet> addWallet(@PathVariable long accountId, @RequestBody WalletCreateRequest walletCreateRequest) throws NotFoundException, NotSupportedException {
        Wallet wallet = accountService.addWallet(accountId, walletCreateRequest);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @PostMapping("/transfer/{fromWallet}/{toWallet}")
    public ResponseEntity<Transaction> transferAmount(@PathVariable @NotNull long fromWallet, @PathVariable  @NotNull long toWallet, @RequestBody TransactionRequest transactionRequest) throws AmountLessThanZeroException, TransactionFailureException, NotFoundException, NotSupportedException {
        if(transactionRequest.getAmount().compareTo(BigDecimal.ZERO) < 0){
            throw new AmountLessThanZeroException("Transaction amount should be greater than 0.0");
        }
        Transaction transaction = transactionService.transferAmount(fromWallet, toWallet, transactionRequest);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PostMapping("/transfer/all")
    public ResponseEntity<List<Transaction>> transferAll(@RequestBody List<ListTransactionRequest> listTransactionRequest) throws AmountLessThanZeroException, TransactionFailureException, NotFoundException, NotSupportedException, ExecutionException, InterruptedException {
        if(listTransactionRequest.stream().anyMatch(a -> a.getAmount().compareTo(BigDecimal.ZERO)<0)){
            throw new AmountLessThanZeroException("Transaction amount should be greater than 0.0");
        }
        List<Transaction> listOfTransaction = transactionService.processAllTransaction(listTransactionRequest);
        return new ResponseEntity<>(listOfTransaction,HttpStatus.OK);
    }

    @PatchMapping("/close/account/{accountId}")
    public ResponseEntity<Void> closeAccount(@PathVariable @NotNull Long accountId) throws NotFoundException, AlreadyClosedException {
        accountService.closeAccount(accountId);
        return  ResponseEntity.ok().build();
    }

    @PutMapping("/changeState/account/{accountId}")
    public ResponseEntity<Void> changeAccountState(@PathVariable @NotNull Long accountId, @RequestParam(required = true) AccountState accountState) throws NotFoundException, AccountStateChangeException {
        accountService.changeAccountState(accountId, accountState);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateBalance/wallet/{walletId}")
    public ResponseEntity<Wallet> updateWalletBalance(@PathVariable @NotNull long walletId, @RequestBody UpdateBalance newBalance) throws NotFoundException, NotSupportedException {
        Wallet wallet = walletService.updateWalletBalance(walletId, newBalance);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }


    @PatchMapping("/transaction/{transactionId}/changeState")
    public ResponseEntity<Transaction> updateTransactionStatus(@PathVariable Long transactionId, @RequestParam TransactionDetails transactionDetails) throws NotSupportedException, NotFoundException {
        if(!List.of(TransactionState.CLEARED, TransactionState.FAILED, TransactionState.PENDING).contains(transactionDetails.getTransactionState())){
            throw new NotSupportedException("Unidentified Transaction State. Please provide from PENDING/CLEARED/FAILED");
        }
        Transaction transaction = transactionService.updateTransactionStatus(transactionId, transactionDetails);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
