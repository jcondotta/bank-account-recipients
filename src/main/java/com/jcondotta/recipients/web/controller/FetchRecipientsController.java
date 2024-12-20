package com.jcondotta.recipients.web.controller;

import com.jcondotta.recipients.service.FetchRecipientService;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RecipientAPIUriBuilder.BANK_ACCOUNT_API_V1_MAPPING)
public class FetchRecipientsController {

    private final FetchRecipientService fetchRecipientService;

    @Inject
    public FetchRecipientsController(FetchRecipientService fetchRecipientService) {
        this.fetchRecipientService = fetchRecipientService;
    }

    @Status(HttpStatus.OK)
    @Get(value = "{?queryParams*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve recipients for a bank account",
            description = "Fetches recipients associated with a given bank account ID. Supports pagination and filtering by recipient name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Recipients successfully retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RecipientsDTO.class))),
            @ApiResponse(responseCode = "204", description = "No recipients found for the provided bank account ID."),
            @ApiResponse(responseCode = "400", description = "Invalid input or query parameters."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public HttpResponse<RecipientsDTO> findRecipients(
            @Schema(description = "The unique identifier of the bank account.", requiredMode = RequiredMode.REQUIRED)
            @NotNull @PathVariable("bank-account-id") UUID bankAccountId,

            @Schema(description = "Query parameters for filtering and pagination.", implementation = QueryParams.class)
            @QueryValue("queryParams") QueryParams queryParams) {

        var queryRecipientsRequest = new QueryRecipientsRequest(bankAccountId, queryParams);
        var recipientsDTO = fetchRecipientService.findRecipients(queryRecipientsRequest);

        if (recipientsDTO.recipients().isEmpty()) {
            return HttpResponse.noContent();
        }

        return HttpResponse.ok().body(recipientsDTO);
    }
}