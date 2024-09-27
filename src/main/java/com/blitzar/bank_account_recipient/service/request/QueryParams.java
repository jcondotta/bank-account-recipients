package com.blitzar.bank_account_recipient.service.request;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

@Serdeable
@Schema(description = "Parameters for querying recipients, including pagination and filtering options.")
public record QueryParams(

        @Schema(description = "The recipientName of the recipient to filter the results. If provided, " +
                "the query will return recipients whose names start with the given value.",
                example = "Jeff")
        Optional<String> recipientName,

        @Schema(description = "The maximum number of results to return. Must be a positive integer.",
                example = "15")
        Optional<Integer> limit,

        @Schema(description = "The last evaluated key used for pagination, " +
                "allowing the query to resume from where the previous one left off.")
        Optional<LastEvaluatedKey> lastEvaluatedKey
) {
}
