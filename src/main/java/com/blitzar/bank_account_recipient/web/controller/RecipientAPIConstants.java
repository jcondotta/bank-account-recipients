package com.blitzar.bank_account_recipient.web.controller;

public interface RecipientAPIConstants {

    String BASE_PATH_API_V1_MAPPING = "/api/v1/recipients/bank-account-id/{bank-account-id}";
    String DELETE_RECIPIENT_API_V1_MAPPING = BASE_PATH_API_V1_MAPPING + "/recipient-name/{recipient-name}";

}
