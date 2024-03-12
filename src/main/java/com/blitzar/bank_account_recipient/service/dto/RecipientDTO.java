package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Introspected
@Serdeable
public record RecipientDTO(
        Long bankAccountId,
        String name,
        String iban,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC") LocalDateTime createdAt) {

    public RecipientDTO(Recipient recipient) {
        this(recipient.getBankAccountId(), recipient.getName(), recipient.getIban(), recipient.getCreatedAt());
    }
}