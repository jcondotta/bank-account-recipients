package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.AddRecipientService;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;

@Validated
@Controller(RecipientAPIConstants.BASE_PATH_API_V1_MAPPING)
public class AddRecipientController {

    private final AddRecipientService addRecipientService;

    public AddRecipientController(AddRecipientService addRecipientService) {
        this.addRecipientService = addRecipientService;
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> addRecipient(@PathVariable("bank-account-id") Long bankAccountId, @Body @Valid AddRecipientRequest addRecipientRequest){
        Recipient recipient = addRecipientService.addRecipient(bankAccountId, addRecipientRequest);
        return HttpResponse.created(recipient);
    }
}