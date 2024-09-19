package com.blitzar.bank_account_recipient.exception;

import java.util.UUID;

public class RecipientNotFoundException extends RuntimeException{

    private UUID bankAccountId;
    private String recipientName;

    public RecipientNotFoundException(String message, UUID bankAccountId, String recipientName) {
        super(message);
        this.bankAccountId = bankAccountId;
        this.recipientName = recipientName;
    }

    public UUID getBankAccountId() {
        return bankAccountId;
    }

    public String getRecipientName() {
        return recipientName;
    }
}
