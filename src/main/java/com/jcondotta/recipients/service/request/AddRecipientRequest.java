package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.validation.annotation.Iban;
import com.jcondotta.recipients.validation.annotation.SecureInput;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "Request object for adding a new recipient.")
public record AddRecipientRequest(
        @Schema(description = "The bank account ID associated with the recipient.",
                example = "e8c8be4e-38b3-4c28-88c2-d22f4ef2e34f",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "recipient.bankAccountId.notNull") UUID bankAccountId,

        @Schema(description = "The recipientName of the recipient.",
                example = "Jefferson Condotta",
                requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "recipient.recipientName.notBlank")
        @Size(max = 50, message = "recipient.recipientName.tooLong")
        @SecureInput(message = "recipient.recipientName.invalid")
        String recipientName,

        @Schema(description = "The IBAN of the recipient.",
                example = "GB29NWBK60161331926819",
                requiredMode = RequiredMode.REQUIRED)
        @Iban
        String recipientIban
) { }