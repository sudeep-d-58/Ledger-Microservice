package com.command.ledger.exception;

public class AlreadyClosedException extends Exception {
    public AlreadyClosedException(String s) {
        super(s);
    }
}
