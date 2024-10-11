package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Serdeable
@Schema(name = "RecipientDTO", description = "Represents a recipient entity with account details.")
public class RecipientDTO {

    private final UUID bankAccountId;
    private final String recipientName;
    private final String recipientIban;
    private final LocalDateTime createdAt;

    public RecipientDTO(
            @JsonProperty("bankAccountId") UUID bankAccountId,
            @JsonProperty("recipientName") String recipientName,
            @JsonProperty("recipientIban") String recipientIban,
            @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.bankAccountId = bankAccountId;
        this.recipientName = recipientName;
        this.recipientIban = recipientIban;
        this.createdAt = createdAt;
    }
    public RecipientDTO(Recipient recipient){
        this(recipient.getBankAccountId(), recipient.getRecipientName(), recipient.getRecipientIban(), recipient.getCreatedAt());
    }

    public UUID getBankAccountId() {
        return bankAccountId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientIban() {
        return recipientIban;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
