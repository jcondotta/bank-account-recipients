package com.jcondotta.recipients.service.dto;

import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

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