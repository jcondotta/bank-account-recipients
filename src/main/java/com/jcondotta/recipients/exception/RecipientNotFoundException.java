package com.jcondotta.recipients.exception;

import java.util.UUID;

public class RecipientNotFoundException extends RuntimeException{

    private final UUID bankAccountId;
    private final String recipientName;

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
