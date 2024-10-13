package com.blitzar.bank_account_recipient.service.request;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "Represents the last evaluated key used for pagination in DynamoDB queries. This key helps in navigating through paginated responses.")
public record LastEvaluatedKey(

        @Schema(description = "The unique identifier for the bank account associated with the recipient.",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull UUID bankAccountId,

        @Schema(description = "The recipientName of the recipient used for pagination.",
                example = "Jefferson Condotta",
                maxLength = 30,
                requiredMode = RequiredMode.REQUIRED)
        @NotBlank String recipientName
) {

    public Map<String, AttributeValue> toExclusiveStartKey() {
        return Map.of(
                "bankAccountId", AttributeValue.fromS(bankAccountId.toString()),
                "recipientName", AttributeValue.fromS(recipientName));
    }
}