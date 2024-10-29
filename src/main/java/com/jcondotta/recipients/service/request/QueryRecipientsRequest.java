package com.jcondotta.recipients.service.request;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Serdeable
@Schema(description = "Request object for querying recipients with query parameters.")
public record QueryRecipientsRequest(
        @Schema(description = "The UUID of the bank account associated with the recipients.",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "recipient.bankAccountId.notNull")
        UUID bankAccountId,

        @Schema(description = "Additional query parameters for pagination or sorting.")
        @Valid
        QueryParams queryParams)
{
        public QueryRecipientsRequest(UUID bankAccountId, QueryParams queryParams) {
                this.bankAccountId = Objects.requireNonNull(bankAccountId, "query.recipients.bankAccountId.notNull");
                this.queryParams = Objects.requireNonNullElseGet(queryParams, () -> QueryParams.builder().build());
        }

        public QueryRecipientsRequest(UUID bankAccountId) {
                this(bankAccountId, null);
        }
}