package com.jcondotta.recipients.helper;

import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;

import java.util.Optional;

public class QueryParamsBuilder {

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
