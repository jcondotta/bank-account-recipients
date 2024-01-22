package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.service.UpdateRecipientService;
import com.blitzar.bank_account_recipient.service.request.UpdateRecipientRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;

@Validated
@Controller(RecipientAPIConstants.DELETE_API_V1_MAPPING)
public class UpdateRecipientController {

    private final UpdateRecipientService updateRecipientService;

    public UpdateRecipientController(UpdateRecipientService updateRecipientService) {
        this.updateRecipientService = updateRecipientService;
    }

    @Put(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> deleteRecipient(@PathVariable("bank-account-id") Long bankAccountId, @PathVariable("recipient-id") String recipientId,
                                           @Body UpdateRecipientRequest updateRecipientRequest){
        updateRecipientService.updateRecipient(bankAccountId, recipientId, updateRecipientRequest);
        return HttpResponse.ok();
    }
}