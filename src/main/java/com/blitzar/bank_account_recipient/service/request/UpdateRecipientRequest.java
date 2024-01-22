package com.blitzar.bank_account_recipient.service.request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Introspected
@Serdeable
public record UpdateRecipientRequest(
        @NotBlank(message = "recipient.name.notBlank") String name,
        @NotBlank(message = "recipient.iban.notBlank") String iban
) { }