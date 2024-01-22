package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Introspected
@Serdeable
public record RecipientDTO(
        String recipientId,
        String name,
        String iban,
        Long bankAccountId,
        LocalDateTime dateCreated) {

    public RecipientDTO(Recipient recipient) {
        this(recipient.getId(), recipient.getName(), recipient.getIban(), recipient.getBankAccountId(), recipient.getDateCreated());
    }
}