package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.service.DeleteRecipientService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;

@Validated
@Controller(RecipientAPIConstants.RECIPIENT_NAME_API_V1_MAPPING)
public class DeleteRecipientController {
    private final DeleteRecipientService deleteRecipientService;

    public DeleteRecipientController(DeleteRecipientService deleteRecipientService) {
        this.deleteRecipientService = deleteRecipientService;
    }

    @Status(HttpStatus.NO_CONTENT)
    @Delete(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> deleteRecipient(@PathVariable("bank-account-id") Long bankAccountId, @PathVariable("recipient-name") String recipientName){
        deleteRecipientService.deleteRecipient(bankAccountId, recipientName);
        return HttpResponse.noContent();
    }
}