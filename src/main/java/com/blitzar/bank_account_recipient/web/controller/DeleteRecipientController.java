package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.service.DeleteRecipientService;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.DeleteRecipientRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@Validated
@Controller(RecipientAPIConstants.RECIPIENT_NAME_API_V1_MAPPING)
public class DeleteRecipientController {

    private final DeleteRecipientService deleteRecipientService;

    public DeleteRecipientController(DeleteRecipientService deleteRecipientService) {
        this.deleteRecipientService = deleteRecipientService;
    }

    @Status(HttpStatus.NO_CONTENT)
    @Delete
    @Operation(summary = "Delete a bank account recipient",
            description = "Deletes a specific recipient from the bank account. "
                    + "Both the bank account ID and the recipient name are required to identify the recipient to delete.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipient successfully deleted. No content is returned as this action does not require a response body."),
            @ApiResponse(responseCode = "404", description = "Recipient not found for the provided bank account ID and recipient name. Ensure the details are correct."),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while attempting to delete the recipient.")
    })
    public HttpResponse<Void> deleteRecipient(
            @Schema(description = "The unique identifier for the bank account.", requiredMode = RequiredMode.REQUIRED)
            @PathVariable("bank-account-id") UUID bankAccountId,

            @Schema(description = "The recipientName of the recipient to be deleted.", requiredMode = RequiredMode.REQUIRED)
            @PathVariable("recipient-name") String recipientName){

        deleteRecipientService.deleteRecipient(new DeleteRecipientRequest(bankAccountId, recipientName));

        return HttpResponse.noContent();
    }
}