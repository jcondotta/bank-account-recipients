package com.blitzar.bank_account_recipient.domain;

import io.micronaut.serde.annotation.Serdeable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.util.UUID;

@Serdeable
@DynamoDbBean
public class Recipient {

    private UUID bankAccountId;

    private String recipientName;

    private String recipientIban;

    private LocalDateTime createdAt;

    public Recipient() { }

    public Recipient(UUID bankAccountId, String recipientName, String recipientIban, LocalDateTime createdAt) {
        this.bankAccountId = bankAccountId;
        this.recipientName = recipientName;
        this.recipientIban = recipientIban;
        this.createdAt = createdAt;
    }

    @DynamoDbPartitionKey
    public UUID getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(UUID bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    @DynamoDbSortKey
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientIban() {
        return recipientIban;
    }

    public void setRecipientIban(String recipientIban) {
        this.recipientIban = recipientIban;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "bankAccountId=" + bankAccountId +
                ", recipientName='" + recipientName + '\'' +
                ", recipientIban='" + recipientIban + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
