package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Introspected
@Serdeable
public record RecipientDTO(
        Long bankAccountId,
        String name,
        String iban,
        LocalDateTime createdAt) {

    public RecipientDTO(Recipient recipient) {
        this(recipient.getBankAccountId(), recipient.getName(), recipient.getIban(), recipient.getCreatedAt());
    }
}