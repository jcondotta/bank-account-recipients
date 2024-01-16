package com.blitzar.bank_account_recipient.service.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Introspected
@Serdeable
public record RecipientsDTO(Collection<RecipientDTO> recipients) {

    @Override
    public Collection<RecipientDTO> recipients() {
        return Objects.nonNull(recipients) ? recipients : new ArrayList<>();
    }
}