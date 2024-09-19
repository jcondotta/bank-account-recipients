package com.blitzar.bank_account_recipient.exception;

public class RecipientNotFoundException extends RuntimeException{

    private Object bankAccountId;
    private String recipientName;

    public RecipientNotFoundException(String message, Object bankAccountId, String recipientName) {
        super(message);
        this.bankAccountId = bankAccountId;
        this.recipientName = recipientName;
    }

    public Object getBankAccountId() {
        return bankAccountId;
    }

    public String getRecipientName() {
        return recipientName;
    }
}
