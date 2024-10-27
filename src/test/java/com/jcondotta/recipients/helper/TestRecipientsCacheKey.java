package com.jcondotta.recipients.helper;

import java.util.UUID;

public enum TestRecipientsCacheKey {

    CACHE_KEY_NO_QUERY_PARAMS("recipients:bank-account-id:01920bff-1338-7efd-ade6-e9128debe5d4:query-params:2c013149ead6d11268ae479e1e3ef90bce4d3c62cfbdbe6545b24263ceeea9d5");

    private final String cacheKey;

    TestRecipientsCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
}