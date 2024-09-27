package com.blitzar.bank_account_recipient.service.dto;

import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@Serdeable
@Schema(description = "A DTO representing a list of recipients with pagination details.")
public record RecipientsDTO(

        @Schema(description = "A list of recipient details.",
                requiredMode = RequiredMode.REQUIRED)
        List<RecipientDTO> recipients,

        @Schema(description = "The total number of recipients returned.",
                example = "25",
                requiredMode = RequiredMode.REQUIRED)
        int count,

        @Schema(description = "The last evaluated key used for pagination, which allows fetching the next page of results.",
                requiredMode = RequiredMode.NOT_REQUIRED)
        LastEvaluatedKey lastEvaluatedKey
) {

    @Override
    public List<RecipientDTO> recipients() {
        return Objects.nonNull(recipients) ? recipients : new ArrayList<>();
    }
}