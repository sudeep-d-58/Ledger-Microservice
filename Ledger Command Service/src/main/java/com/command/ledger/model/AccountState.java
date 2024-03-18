package com.command.ledger.model;

public enum AccountState {

    OPEN("Open"),
    CLOSED("Closed");

    private final String description;

    AccountState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
