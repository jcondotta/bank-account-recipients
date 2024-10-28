package com.jcondotta.recipients.service.request;

import com.google.common.hash.Hashing;
import com.jcondotta.recipients.validation.annotation.SecureInput;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Serdeable
@Schema(description = "Parameters for querying recipients, including pagination and filtering options.")
public record QueryParams(

        @Schema(description = "The recipientName of the recipient to filter the results. If provided, " +
                "the query will return recipients whose names start with the given value.",
                example = "Jeff")
        Optional<
                @Size(max = 50, message = "query.recipients.params.recipientName.tooLong")
                @SecureInput(message = "query.recipients.params.recipientName.invalid")
                String> recipientName,

        @Schema(description = "The maximum number of results to return. Must be a positive integer.", example = "15")
        Optional<@Max(value = 20, message = "query.recipients.params.limit.exceedsMaximum") Integer> limit,

        @Schema(description = "The last evaluated key used for pagination, " + "allowing the query to resume from where the previous one left off.")
        Optional<@Valid LastEvaluatedKey> lastEvaluatedKey
) {

    public static QueryParamsBuilder builder() {
        return new QueryParamsBuilder();
    }

    public static class QueryParamsBuilder {

        private Optional<String> recipientName = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private Optional<LastEvaluatedKey> lastEvaluatedKey = Optional.empty();

        public QueryParamsBuilder withRecipientName(String recipientName) {
            this.recipientName = Optional.ofNullable(recipientName);
            return this;
        }

        public QueryParamsBuilder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public QueryParamsBuilder withLastEvaluatedKey(LastEvaluatedKey lastEvaluatedKey) {
            this.lastEvaluatedKey = Optional.ofNullable(lastEvaluatedKey);
            return this;
        }

        public QueryParams build() {
            return new QueryParams(recipientName, limit, lastEvaluatedKey);
        }
    }

    public String getHashSHA256(){
        return Hashing.sha256().hashString(this.toString(), StandardCharsets.UTF_8).toString();
    }

    @Override
    public String toString() {
        return "QueryParams{" +
                "recipientName=" + recipientName +
                ", limit=" + limit +
                ", lastEvaluatedKey=" + lastEvaluatedKey +
                '}';
    }
}
