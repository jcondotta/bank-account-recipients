package com.blitzar.bank_account_recipient.service.request;

import com.blitzar.bank_account_recipient.validation.annotation.SecureInput;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "Request object for deleting a recipient.")
public record DeleteRecipientRequest(
        @Schema(description = "The UUID of the bank account associated with the recipient.",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "recipient.bankAccountId.notNull")
        UUID bankAccountId,

        @Schema(description = "The recipientName of the recipient to be deleted.",
                example = "Jefferson Condotta",
                requiredMode = RequiredMode.REQUIRED)
        @SecureInput(message = "recipient.recipientName.invalid")
        @NotBlank(message = "recipient.recipientName.notBlank")
        String recipientName) {
}