package com.blitzar.bank_account_recipient.domain;

import io.micronaut.serde.annotation.Serdeable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;

@Serdeable
@DynamoDbBean
public class Recipient {

    private Long bankAccountId;

    private String name;

    private String iban;

    private LocalDateTime createdAt;

    public Recipient() { }

    public Recipient(Long bankAccountId, String name, String iban, LocalDateTime createdAt) {
        this.bankAccountId = bankAccountId;
        this.name = name;
        this.iban = iban;
        this.createdAt = createdAt;
    }

    @DynamoDbPartitionKey
    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getName() {
        return name;
    }

    @DynamoDbSortKey
    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
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
                ", name='" + name + '\'' +
                ", iban='" + iban + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
