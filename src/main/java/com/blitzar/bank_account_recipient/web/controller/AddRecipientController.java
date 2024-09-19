package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.AddRecipientService;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.UUID;

@Validated
@Controller(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
public class AddRecipientController {

    private final AddRecipientService addRecipientService;

    public AddRecipientController(AddRecipientService addRecipientService) {
        this.addRecipientService = addRecipientService;
    }

    @Status(HttpStatus.CREATED)
    @Post(consumes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Adds a new recipient", description = "Adds a new recipient to a bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Recipient.class))),
            @ApiResponse(responseCode = "400", description = "Invalid AddRecipientRequest supplied")
    })
    public HttpResponse<?> addRecipient(@PathVariable("bank-account-id") UUID bankAccountId, @Body @Valid AddRecipientRequest addRecipientRequest){
        Recipient recipient = addRecipientService.addRecipient(bankAccountId, addRecipientRequest);
        return HttpResponse.created(recipient);
    }
}