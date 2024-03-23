package com.command.ledger.controller;

import com.command.ledger.exception.*;
import com.command.ledger.model.*;
import com.command.ledger.service.AccountService;
import com.command.ledger.service.TransactionService;
import com.command.ledger.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Account Creation", description = "Account Creation.Account can be created without an Wallet Being Present. An entity can have multiple accounts. No checks")
    @PostMapping(value = "/create/account", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Account> createAccount(@RequestBody AccountCreateRequest accountCreateRequest){
        Account account = accountService.createAccount(accountCreateRequest);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @Operation(summary = "Wallet Addition to Account", description = "This adds Balance to a wallet amd Also Transmits event to wallet-data-publisher-1 topic")
    @PostMapping("/add/wallet/{accountId}")
    public ResponseEntity<Wallet> addWallet(@PathVariable long accountId, @RequestBody WalletCreateRequest walletCreateRequest) throws NotFoundException, NotSupportedException, AmountLessThanZeroException {
        if(walletCreateRequest.getBalance().compareTo(BigDecimal.ZERO) < 0){
            throw new AmountLessThanZeroException("Balance amount should be greater than 0.0");
        }
        Wallet wallet = accountService.addWallet(accountId, walletCreateRequest);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @Operation(summary = "Transfer Amount", description = "Used for Single Transaction. Updates the Wallet and Transaction DB. Emits Event to transaction-data-publisher-1 for success and failure both.\n" +
            "Maintains ACID Properties w.r.t Wallet Balance. Emits event to wallet-data-publisher-1 which is consumed by Query Service for updating Historical Balance.")
    @PostMapping("/transfer/{fromWallet}/{toWallet}")
    public ResponseEntity<Transaction> transferAmount(@PathVariable @NotNull long fromWallet, @PathVariable  @NotNull long toWallet, @RequestBody TransactionRequest transactionRequest) throws AmountLessThanZeroException, TransactionFailureException, NotFoundException, NotSupportedException {
        if(transactionRequest.getAmount().compareTo(BigDecimal.ZERO) < 0){
            throw new AmountLessThanZeroException("Transaction amount should be greater than 0.0");
        }
        Transaction transaction = transactionService.transferAmount(fromWallet, toWallet, transactionRequest);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @Operation(summary = "Transfers Balance Between wallets in Async Mode(Simultaneously)", description = "Used for Multiple Transaction at same time(async way). Maintains All or Noting in Partial Manner. For any failure in Transaction is the request , It will generate a Failed\n" +
            "Transaction Object and for the rest it will complete the Transaction and return all the transaction result (Both Failed and Success). It will still maintain the ACID property as\n" +
            "Wallet balances are consistent after combination of success and failures as well. Can be implemented as Roll Back of All transaction if failure Occurred if needed. Emits Event to transaction-data-publisher-1 for success and failure both.\n" +
            "Maintains ACID Properties w.r.t Wallet Balance. Emits event to wallet-data-publisher-1 which is consumed by Query Service for updating Historical Balance.Uses Spring Retry Module\n" +
            "for 3 times retry and then generate failed transaction.")
    @PostMapping("/transfer/all")
    public ResponseEntity<List<Transaction>> transferAll(@RequestBody List<ListTransactionRequest> listTransactionRequest) throws AmountLessThanZeroException, TransactionFailureException, NotFoundException, NotSupportedException, ExecutionException, InterruptedException {
        if(listTransactionRequest.stream().anyMatch(a -> a.getAmount().compareTo(BigDecimal.ZERO)<0)){
            throw new AmountLessThanZeroException("Transaction amount should be greater than 0.0");
        }
        List<Transaction> listOfTransaction = transactionService.processAllTransaction(listTransactionRequest);
        return new ResponseEntity<>(listOfTransaction,HttpStatus.OK);
    }

    @Operation(summary = "Close Account", description = "If account is not already closed, then it closes the account and updates DB. Does not emits any event (Can be done if needed)")
    @PatchMapping("/close/account/{accountId}")
    public ResponseEntity<Void> closeAccount(@PathVariable @NotNull Long accountId) throws NotFoundException, AlreadyClosedException {
        accountService.closeAccount(accountId);
        return  ResponseEntity.ok().build();
    }

    @PatchMapping("/changeState/account/{accountId}")
    public ResponseEntity<Void> changeAccountState(@PathVariable @NotNull Long accountId, @RequestBody EnumWrapper accountState) throws NotFoundException, AccountStateChangeException, NotSupportedException {
        if(!List.of(AccountState.CLOSED.getDescription(), AccountState.OPEN.getDescription()).contains(accountState.getValue())){
            throw new NotSupportedException("Unidentified Account State. Please provide from Open/Closed");
        }
        accountService.changeAccountState(accountId, accountState.getValue().equalsIgnoreCase(AccountState.CLOSED.getDescription()) ? AccountState.CLOSED: AccountState.OPEN);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update Wallet Balance", description = "To keep simple, no Checks present for Updating Balance except for Wallet Presence and Account being not Closed (Logic can be improved to manage balance). Emits event to wallet-data-publisher-1 topic")
    @PatchMapping("/updateBalance/wallet/{walletId}")
    public ResponseEntity<Wallet> updateWalletBalance(@PathVariable @NotNull long walletId, @RequestBody UpdateBalance newBalance) throws NotFoundException, NotSupportedException {
        Wallet wallet = walletService.updateWalletBalance(walletId, newBalance);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }


    @Operation(summary = "Transaction Status Update", description = "To keep simple, no Checks present for Updating Transaction State for Transaction Presence in DB  (Logic can be improved to manage better). Emits event to transaction-data-publisher-2 topic")
    @PatchMapping("/transaction/{transactionId}/changeState")
    public ResponseEntity<Transaction> updateTransactionStatus(@PathVariable @NotNull Long transactionId, @RequestBody TransactionDetails transactionDetails) throws NotSupportedException, NotFoundException {
        if(!List.of(TransactionState.CLEARED, TransactionState.FAILED, TransactionState.PENDING).contains(transactionDetails.getTransactionState())){
            throw new NotSupportedException("Unidentified Transaction State. Please provide from PENDING/CLEARED/FAILED");
        }
        Transaction transaction = transactionService.updateTransactionStatus(transactionId, transactionDetails);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
