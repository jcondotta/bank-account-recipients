package com.blitzar.bank_account_recipient.service.request;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Serdeable
@Schema(description="Pet description")
public record AddRecipientRequest(
        @NotBlank(message = "recipient.name.notBlank")
        @Schema(description="Recipient name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @NotBlank(message = "recipient.iban.notBlank")
        @Schema(description="Recipient IBAN", requiredMode = Schema.RequiredMode.REQUIRED)
        String iban
) { }