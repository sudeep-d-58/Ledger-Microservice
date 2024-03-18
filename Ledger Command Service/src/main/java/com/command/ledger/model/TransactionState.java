package com.command.ledger.model;

public enum TransactionState {
    PENDING("Pending"),
    CLEARED("Cleared"),
    FAILED("Failed");
    private final String description;

    TransactionState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
