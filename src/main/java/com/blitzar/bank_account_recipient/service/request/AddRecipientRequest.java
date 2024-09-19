package com.blitzar.bank_account_recipient.service.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record AddRecipientRequest(
        @NotBlank(message = "recipient.name.notBlank") String name,
        @NotBlank(message = "recipient.iban.notBlank") String iban
) { }