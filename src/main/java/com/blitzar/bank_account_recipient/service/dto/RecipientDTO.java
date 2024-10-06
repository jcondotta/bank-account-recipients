package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(name = "RecipientDTO", description = "Represents a recipient entity with account details.")
public record RecipientDTO(

        @Schema(description = "Unique identifier for the bank account.",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                requiredMode = RequiredMode.REQUIRED)
        UUID bankAccountId,

        @Schema(description = "Name of the recipient.",
                example = "Jefferson Condotta",
                maxLength = 30,
                requiredMode = RequiredMode.REQUIRED)
        String recipientName,

        @Schema(description = "IBAN of the recipient.",
                example = "GB29NWBK60161331926819",
                maxLength = 34,
                requiredMode = RequiredMode.REQUIRED)
        String recipientIban,

        @Schema(description = "Timestamp when the recipient was created.",
                example = "2023-08-23T14:55:00Z",
                requiredMode = RequiredMode.REQUIRED)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        LocalDateTime createdAt
) {

    public RecipientDTO(Recipient recipient) {
        this(recipient.getBankAccountId(), recipient.getRecipientName(), recipient.getRecipientIban(), recipient.getCreatedAt());
    }
}