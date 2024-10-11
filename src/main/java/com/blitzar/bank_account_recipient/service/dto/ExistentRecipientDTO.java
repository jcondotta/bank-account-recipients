package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Introspected
@Schema(name = "ExistentRecipientDTO", description = "Represents an existing recipient entity returned when the recipient already exists.")
public class ExistentRecipientDTO extends RecipientDTO{

    public ExistentRecipientDTO(UUID bankAccountId, String recipientName, String recipientIban, LocalDateTime createdAt) {
        super(bankAccountId, recipientName, recipientIban, createdAt);
    }

    public ExistentRecipientDTO(Recipient recipient) {
        super(recipient);
    }
}
