package com.blitzar.bank_account_recipient.web.controller;

public interface RecipientAPIConstants {

    String RECIPIENTS_BASE_PATH_API_V1_MAPPING = "/api/v1/recipients";
    String BANK_ACCOUNT_API_V1_MAPPING = RECIPIENTS_BASE_PATH_API_V1_MAPPING + "/bank-account-id/{bank-account-id}";
    String RECIPIENT_NAME_API_V1_MAPPING = BANK_ACCOUNT_API_V1_MAPPING + "/recipient-name/{recipient-name}";

}
