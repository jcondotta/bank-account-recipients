package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.validation.annotation.SecureInput;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
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
        @NotNull(message = "query.recipients.params.lastEvaluatedKey.bankAccountId.notNull")
        UUID bankAccountId,

        @Schema(description = "The recipientName of the recipient used for pagination.",
                example = "Jefferson Condotta",
                maxLength = 50,
                requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "query.recipients.params.lastEvaluatedKey.recipientName.notBlank")
        @Size(max = 50, message = "query.recipients.params.lastEvaluatedKey.recipientName.tooLong")
        @SecureInput(message = "query.recipients.params.lastEvaluatedKey.recipientName.invalid")
        String recipientName
) {

    public Map<String, AttributeValue> toExclusiveStartKey() {
        if (bankAccountId == null) {
            throw new IllegalArgumentException("bankAccountId must not be null");
        }
        if (StringUtils.isBlank(recipientName)) {
            throw new IllegalArgumentException("recipientName must not be null or blank");
        }

        return Map.of(
                "bankAccountId", AttributeValue.fromS(bankAccountId.toString()),
                "recipientName", AttributeValue.fromS(recipientName)
        );
    }

    @Override
    public String toString() {
        return "LastEvaluatedKey{" +
                "bankAccountId=" + bankAccountId +
                ", recipientName='" + recipientName + '\'' +
                '}';
    }
}