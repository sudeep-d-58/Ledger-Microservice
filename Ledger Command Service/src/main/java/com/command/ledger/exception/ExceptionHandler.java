package com.command.ledger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidInput(MethodArgumentNotValidException exception) {
        Map<String, String> map = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(err -> {
            map.put(err.getField(), err.getDefaultMessage());
        });
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInsufficientFundsException(InsufficientFundsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AlreadyClosedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAlreadyClosedException(AlreadyClosedException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(TransactionFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleTransactionFailureException(TransactionFailureException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccountStateChangeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAccountStateChangeException(AccountStateChangeException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AmountLessThanZeroException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAmountLessThanZeroException(AmountLessThanZeroException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotSupportedException(NotSupportedException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        return map;
    }
}
