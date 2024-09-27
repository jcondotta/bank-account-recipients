package com.blitzar.bank_account_recipient.helper;

import java.util.UUID;

public enum TestBankAccount {

    BRAZIL(UUID.fromString("01920bff-1338-7efd-ade6-e9128debe5d4")),
    ITALY(UUID.fromString("01921f7f-5672-70ac-8c7e-6d7a941706cb")),
    SPAIN(UUID.fromString("01922e2b-b77f-7716-86ff-925aa6bd61fd")),
    NORWAY(UUID.fromString("01922e41-7eee-7958-924f-09e8919a7887"));

    private final UUID bankAccountId;

    TestBankAccount(UUID bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public UUID getBankAccountId() {
        return bankAccountId;
    }
}