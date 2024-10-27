package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.request.QueryParams;

import java.util.Objects;
import java.util.UUID;

public record RecipientsCacheKey(UUID bankAccountId, QueryParams queryParams) {

    public static final String RECIPIENTS_CACHE_KEY_TEMPLATE = "recipients:bank-account-id:%s:query-params:%s";

    public RecipientsCacheKey(UUID bankAccountId, QueryParams queryParams) {
        this.bankAccountId = Objects.requireNonNull(bankAccountId, "cache.recipients.bankAccountId.notNull");
        this.queryParams = Objects.requireNonNullElseGet(queryParams, () -> new QueryParams());
    }

    public RecipientsCacheKey(UUID bankAccountId) {
        this(bankAccountId, null);
    }

    public String getCacheKey(){
        return String.format(RECIPIENTS_CACHE_KEY_TEMPLATE, bankAccountId, queryParams.getHashSHA256());
    }
}
