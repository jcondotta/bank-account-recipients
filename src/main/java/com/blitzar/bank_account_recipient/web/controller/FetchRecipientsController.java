package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.service.FetchRecipientService;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

@Validated
@Controller(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
public class FetchRecipientsController {

    private final FetchRecipientService fetchRecipientService;

    @Inject
    public FetchRecipientsController(FetchRecipientService fetchRecipientService) {
        this.fetchRecipientService = fetchRecipientService;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> findRecipients(@PathVariable("bank-account-id") Long bankAccountId){
        RecipientsDTO recipientsDTO = fetchRecipientService.findRecipients(bankAccountId);
        return HttpResponse.ok().body(recipientsDTO);
    }
}