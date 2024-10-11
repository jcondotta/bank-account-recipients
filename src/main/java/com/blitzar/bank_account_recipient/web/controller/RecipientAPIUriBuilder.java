package com.blitzar.bank_account_recipient.web.controller;

import io.micronaut.http.uri.UriBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

public interface RecipientAPIUriBuilder {

    String RECIPIENTS_BASE_PATH_API_V1_MAPPING = "/api/v1/recipients";
    String BANK_ACCOUNT_API_V1_MAPPING = RECIPIENTS_BASE_PATH_API_V1_MAPPING + "/bank-account-id/{bank-account-id}";
    String RECIPIENT_NAME_API_V1_MAPPING = BANK_ACCOUNT_API_V1_MAPPING + "/recipient-name/{recipient-name}";

    static URI fetchRecipientsURI(UUID bankAccountId) {
        return UriBuilder.of(BANK_ACCOUNT_API_V1_MAPPING)
                .expand(Map.of("bank-account-id", bankAccountId.toString()));
    }

    static URI deleteRecipientsURI(UUID bankAccountId, String recipientName) {
        return UriBuilder.of(RECIPIENT_NAME_API_V1_MAPPING)
                .expand(Map.of(
                        "bank-account-id", bankAccountId.toString(),
                        "recipient-name", recipientName
                ));
    }
}
