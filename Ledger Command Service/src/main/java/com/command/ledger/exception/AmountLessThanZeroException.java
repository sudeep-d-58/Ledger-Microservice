package com.command.ledger.exception;

public class AmountLessThanZeroException extends Exception {
    public AmountLessThanZeroException(String s) {
        super(s);
    }
}
