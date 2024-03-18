package com.command.ledger.service;

import com.command.ledger.exception.InsufficientFundsException;
import com.command.ledger.exception.NotFoundException;
import com.command.ledger.exception.NotSupportedException;
import com.command.ledger.exception.TransactionFailureException;
import com.command.ledger.model.*;
import com.command.ledger.publisher.MessagePublisher;
import com.command.ledger.repository.TransactionRepository;
import com.command.ledger.util.Utility;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private final Object lock = new Object();

    @Autowired
    private WalletService walletService;

    @Autowired
    private MessagePublisher messagePublisher;


    @Transactional(rollbackOn = Exception.class)
    @Retryable(value = {DataAccessException.class, OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Transaction transferAmount(long fromWalletId, long toWalletId, TransactionRequest transactionRequest) throws NotFoundException, NotSupportedException, TransactionFailureException {
        Wallet fromWallet = walletService.getWalletById(fromWalletId);
        Wallet toWallet = walletService.getWalletById(toWalletId);

        validateWalletsForTransfer(fromWallet, toWallet);

        int retryCount = 0;
        while (true) {
            try {
                Transaction newTransaction = tryUpdateBalancesAndCompleteTransaction(fromWallet, toWallet, transactionRequest.getAmount());
                MessageEvent messageEvent = new MessageEvent(Utility.Transaction_Data_Publisher_1, newTransaction);
                messagePublisher.publish(messageEvent);
                log.info("Transaction Details after transfer : " + newTransaction.toString());
                return newTransaction;
            } catch (Exception ex) {
                log.warn("Exception during transfer (attempt {}). Retrying...", ++retryCount);
                if (retryCount >= 3) {
                    Transaction failedTransaction = createFailedTransaction(fromWallet, toWallet, transactionRequest.getAmount());
                    MessageEvent messageEvent = new MessageEvent(Utility.Transaction_Data_Publisher_1, failedTransaction);
                    messagePublisher.publish(messageEvent);
                    log.info("Transaction Details after failed transfer : " + failedTransaction.toString());
                    throw new TransactionFailureException("Failed to complete transfer after retries. Please try again later.");
                }
            }
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private Transaction tryUpdateBalancesAndCompleteTransaction(Wallet fromWallet, Wallet toWallet, BigDecimal amount) throws InsufficientFundsException, NotFoundException {
        synchronized (lock) {
            HistoricalBalance debit = walletService.debit(fromWallet.getWalletId(), amount);
            HistoricalBalance credit = walletService.credit(toWallet.getWalletId(), amount);
            log.info("from Wallet Details after deduct : " + fromWallet.toString());
            log.info("to Wallet Details after credit : " + toWallet.toString());
            Transaction transaction = new Transaction(fromWallet, toWallet, amount, TransactionState.CLEARED);
            transactionRepository.save(transaction);
            debit.setTransactionId(transaction.getTransactionId());
            credit.setTransactionId(transaction.getTransactionId());
            MessageEvent debitEvent = new MessageEvent(Utility.Wallet_Data_Publisher_1, debit);
            MessageEvent creditEvent = new MessageEvent(Utility.Wallet_Data_Publisher_1, credit);
            messagePublisher.publish(debitEvent);
            messagePublisher.publish(creditEvent);
            return transaction;
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private Transaction createFailedTransaction(Wallet fromWallet, Wallet toWallet, BigDecimal amount) {
        Transaction failedTransaction = new Transaction(fromWallet, toWallet, amount, TransactionState.FAILED);
        return transactionRepository.save(failedTransaction);
    }

    private void validateWalletsForTransfer(Wallet fromWallet, Wallet toWallet) throws NotSupportedException {
        if (fromWallet.getAccount().getAccountState() == AccountState.CLOSED) {
            log.warn("Cannot transfer from a wallet associated with a closed account: {}", fromWallet.getWalletId());
            throw new NotSupportedException("Cannot transfer from a wallet associated with a closed account.");
        }
        if (toWallet.getAccount().getAccountState() == AccountState.CLOSED) {
            log.info("Wallet with id : " + fromWallet.getWalletId() + " belongs to a closed account with accountId : " + fromWallet.getAccount().getAccountId() + " .Try transferring to different wallet belonging to open account");
            throw new NotSupportedException("Wallet with id : " + fromWallet.getWalletId() + " belongs to a closed account with accountId : " + fromWallet.getAccount().getAccountId() + " .Try transferring to different wallet belonging to open account");
        }
    }

    @Async("asyncExecutor")
    @Transactional
    public List<CompletableFuture<Transaction>> processTransactionAsync(List<ListTransactionRequest> listTransactionRequests) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Transaction>> futures = new ArrayList<>();
        for (ListTransactionRequest req : listTransactionRequests) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return transferAmount(req.getFromWallet(), req.getToWallet(), new TransactionRequest(req.getAmount()));
                } catch (NotFoundException | NotSupportedException | TransactionFailureException e) {
                    Wallet fromWallet = null;
                    Wallet toWallet = null;
                    Transaction failedTransaction = null;
                    MessageEvent messageEvent = new MessageEvent();
                    try {
                        fromWallet = walletService.getWalletById(req.getFromWallet());
                        toWallet = walletService.getWalletById(req.getToWallet());
                    } catch (NotFoundException ex) {
                        failedTransaction = createFailedTransaction(fromWallet, toWallet, req.getAmount());
                        messageEvent.setDestination(Utility.Transaction_Data_Publisher_1);
                        messageEvent.setPayload(failedTransaction);
                        messagePublisher.publish(messageEvent);
                        return failedTransaction;
                    }
                    failedTransaction = createFailedTransaction(fromWallet, toWallet, req.getAmount());
                    messageEvent.setDestination(Utility.Transaction_Data_Publisher_1);
                    messageEvent.setPayload(failedTransaction);
                    messagePublisher.publish(messageEvent);
                    return failedTransaction;
                }
            }));
        }
        return futures;
    }

    @Transactional
    public List<Transaction> processAllTransaction(List<ListTransactionRequest> listTransactionRequests) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Transaction>> processTransactionAsync = processTransactionAsync(listTransactionRequests);
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(processTransactionAsync.toArray(new CompletableFuture[0]));
        List<Transaction> processedTransactions = new ArrayList<>();
        for (CompletableFuture<Transaction> future : processTransactionAsync) {
            processedTransactions.add(future.get());
        }
        return processedTransactions;
    }

    public Transaction updateTransactionStatus(long transactionId, TransactionDetails transactionDetails) throws NotFoundException {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setTransactionState(transactionDetails.getTransactionState());
        Transaction transaction1 = transactionRepository.save(transaction);
        MessageEvent messageEvent = new MessageEvent(Utility.Transaction_Data_Publisher_2, transaction1);
        messagePublisher.publish(messageEvent);
        return transaction1;
    }

    public Transaction getTransactionById(long transactionId) throws NotFoundException {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new NotFoundException("Transaction does not exist for transactionId : " + transactionId));
    }

}
