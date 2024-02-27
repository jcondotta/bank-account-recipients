package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.service.DeleteRecipientService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;

@Validated
@Controller(RecipientAPIConstants.GET_RECIPIENT_API_V1_MAPPING)
public class DeleteRecipientController {
    private final DeleteRecipientService deleteRecipientService;

    public DeleteRecipientController(DeleteRecipientService deleteRecipientService) {
        this.deleteRecipientService = deleteRecipientService;
    }

    @Delete(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> deleteRecipient(@PathVariable("bank-account-id") Long bankAccountId, @PathVariable("recipient-id") String recipientId){
        deleteRecipientService.deleteRecipient(bankAccountId, recipientId);
        return HttpResponse.noContent();
    }
}